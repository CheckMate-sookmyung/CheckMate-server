package checkmate.com.checkmate.eventAttendance.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.*;
import checkmate.com.checkmate.mail.component.EmailSender;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.global.component.ExcelGenerator;
import checkmate.com.checkmate.global.component.ExcelReader;
import checkmate.com.checkmate.global.component.PdfGenerator;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static checkmate.com.checkmate.global.codes.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class EventAttendanceService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceRepository eventAttendanceRepository;
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final ExcelReader excelReader;
    @Autowired
    private final S3Uploader s3Uploader;
    @Autowired
    private final PdfGenerator pdfGenerator;
    @Autowired
    private final EmailSender emailSender;
    @Autowired
    private final ExcelGenerator excelGenerator;

    @Transactional
    public StudentInfoResponseDto getStudentInfoByStudentNumber(Accessor accessor, Long eventId, int studentNumber, String eventDate) throws StudentAlreadyAttendedException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(accessor.getMemberId(), eventId);
        if (event == null) {
            throw new GeneralException(EVENT_NOT_FOUND);
        } else {
            String eventTitle = event.getEventTitle();
            Long eventScheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate); //하루에 두 번 하는 행사는 없겠지..?
            EventAttendance studentInfoFromEventAttendance = eventAttendanceRepository.findByEventScheduleIdAndStudentNumber(eventScheduleId, studentNumber);
            if (studentInfoFromEventAttendance == null) {
                throw new GeneralException(STUDENT_NOT_FOUND);
            } else if (!studentInfoFromEventAttendance.getSign().isEmpty()) {
                throw new GeneralException(STUDENT_ALREADY_CHECK);
            } else {
                String maskedName = maskMiddleName(studentInfoFromEventAttendance.getStudent().getStudentName());
                return StudentInfoResponseDto.of(studentInfoFromEventAttendance, eventTitle, maskedName);
            }
        }
    }

    @Transactional
    public List<StrangerInfoResponseDto> getStrangerInfoByPhoneNumberSuffix(Accessor accessor, Long eventId, String phoneNumberSuffix, String eventDate) throws StudentAlreadyAttendedException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        if (event == null) {
            throw new GeneralException(EVENT_NOT_FOUND);
        }

        String eventTitle = event.getEventTitle();
        Long eventScheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);
        List<EventAttendance> strangerInfosFromEventAttendance = eventAttendanceRepository.findAllByEventScheduleIdAndPhoneNumberSuffix(eventScheduleId, phoneNumberSuffix);
        if (strangerInfosFromEventAttendance == null || strangerInfosFromEventAttendance.isEmpty()) {
            throw new GeneralException(STUDENT_NOT_FOUND);
        }

        // 출석한 학생 제거
        strangerInfosFromEventAttendance.removeIf(EventAttendance::isAttendance);
        // 모든 학생이 이미 출석했다면 예외 발생
        if (strangerInfosFromEventAttendance.isEmpty()) {
            throw new StudentAlreadyAttendedException("STUDENT_ALREADY_CHECK");
        }

        List<StrangerInfoResponseDto> responseList = new ArrayList<>();
        for (EventAttendance strangerInfo : strangerInfosFromEventAttendance) {
            String maskedName = maskMiddleName(strangerInfo.getStranger().getStrangerName());
            responseList.add(StrangerInfoResponseDto.of(strangerInfo, eventTitle, maskedName));
        }

        return responseList;
    }


    @Transactional
    public void postSign(Accessor accessor, Long eventId, Long studentInfoId, MultipartFile signImage) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(studentInfoId);
        Event event = eventRepository.findByMemberIdAndEventId(accessor.getMemberId(), eventId);

        if (eventAttendance == null)
            throw new GeneralException(STUDENT_NOT_FOUND);
        String imageUrl = null;
        if (signImage != null) {
            imageUrl = s3Uploader.saveFile(signImage, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(eventId) + "/sign");
            eventAttendance.updateAttendanceByAttendanceCheck(imageUrl);
        } else
            throw new GeneralException(IMAGE_IS_NULL);
    }

    @Transactional
    public List<EventAttendance> readAndSaveAttendanceList(MultipartFile attendanceListFile, EventSchedule eventSchedule, EventTarget eventTarget) throws IOException {
        List<EventAttendance> eventAttendances = new ArrayList<>();
        try {
            if (eventTarget == EventTarget.INTERNAL) {
                eventAttendances = excelReader.readAndSaveAttendanceListAboutStudent(convertMultiPartToFile(attendanceListFile), eventSchedule);
            } else {
                eventAttendances = excelReader.readAndSaveAttendanceListAboutStranger(convertMultiPartToFile(attendanceListFile), eventSchedule);
            }
        } catch (IOException e) {
            throw new GeneralException(IO_EXCEPTION);
        }
        return eventAttendances;
    }


    @Transactional
    public List<String> downloadAttendanceList(Accessor accessor, Long eventId) throws IOException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        List<String> filenames = new ArrayList<>();
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        String eventTitle = event.getEventTitle();
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        MultipartFile attendanceListEachMultipartFile;
        attendanceListEachMultipartFile = pdfGenerator.generateEventAttendanceListPdf(event, eventSchedules);
        int completionTime = event.getCompletionTime();
        MultipartFile attendanceListTotalMultipartFile = excelGenerator.generateExcel(event, eventSchedules, completionTime);
        List<MultipartFile> files = new ArrayList<>();
        files.add(attendanceListEachMultipartFile);
        files.add(attendanceListTotalMultipartFile);
        String originalFilename = attendanceListEachMultipartFile.getOriginalFilename();
        String attendanceListEachUrl = s3Uploader.saveFile(attendanceListEachMultipartFile, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(event.getEventId()));
        event.updateAttendanceListFile(attendanceListEachUrl, null);
        filenames.add(attendanceListEachUrl);
        filenames.add(originalFilename);
        return filenames;
    }

    public void sendAttendanceList(Accessor accessor, Long eventId) throws IOException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        String eventTitle = event.getEventTitle();
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        MultipartFile attendanceListEachMultipartFile = pdfGenerator.generateEventAttendanceListPdf(event, eventSchedules);
        MultipartFile attendanceListTotalMultipartFile = null;
            int completion = event.getCompletionTime();
            attendanceListTotalMultipartFile = excelGenerator.generateExcel(event, eventSchedules, completion);
        List<MultipartFile> files = new ArrayList<>();
        files.add(attendanceListEachMultipartFile);
        files.add(attendanceListTotalMultipartFile);
        emailSender.sendAttendanceListFile(event, files);
        String attendanceListEachUrl = s3Uploader.saveFile(attendanceListEachMultipartFile, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(event.getEventId()));
        String attendanceTotalListUrl = s3Uploader.saveFile(attendanceListTotalMultipartFile, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(event.getEventId()));
        event.updateAttendanceListFile(attendanceListEachUrl, attendanceTotalListUrl);
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile(file.getOriginalFilename(), null);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    @Transactional
    public List<Object> updateAttendanceList(Accessor accessor, Long eventId, List<EventAttendanceRequestDto> eventAttendanceRequestDtos) {
        // eventAttendanceListId가 userId, eventId랑 다 맞는지 확인
        Event event = eventRepository.findByMemberIdAndEventId(accessor.getMemberId(), eventId);
        List<Object> eventAttendanceResponseDtos = new ArrayList<>();

        if (event.getEventTarget() == EventTarget.INTERNAL) {
            List<?> studentEventAttendanceResponseDtos = new ArrayList<>();
            for (EventAttendanceRequestDto eventAttendanceListrequestDto : eventAttendanceRequestDtos) {
                EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(eventAttendanceListrequestDto.getStudentInfoId());
                eventAttendance.updateAttendanceByManager(eventAttendanceListrequestDto.getAttendace());
                eventAttendanceRepository.save(eventAttendance);
                eventAttendanceResponseDtos.add(StudentEventAttendanceResponseDto.of(eventAttendance));
            }
        }
        else{
            List<?> strangerEventAttendanceResponseDtos = new ArrayList<>();
            for (EventAttendanceRequestDto eventAttendanceListrequestDto : eventAttendanceRequestDtos) {
                EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(eventAttendanceListrequestDto.getStudentInfoId());
                eventAttendance.updateAttendanceByManager(eventAttendanceListrequestDto.getAttendace());
                eventAttendanceRepository.save(eventAttendance);
                eventAttendanceResponseDtos.add(StrangerEventAttendanceResponseDto.of(eventAttendance));
            }
        }

        return eventAttendanceResponseDtos;
    }

    private String maskMiddleName(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }

        if (name.length() == 2) {
            return name.charAt(0) + "O";
        }

        char[] nameChars = name.toCharArray();
        nameChars[1] = 'O';
        return new String(nameChars);
    }


}
