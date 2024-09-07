package checkmate.com.checkmate.eventAttendance.service;

import checkmate.com.checkmate.eventAttendance.dto.EventAttendanceListRequestDto;
import checkmate.com.checkmate.eventAttendance.dto.EventAttendanceListResponseDto;
import checkmate.com.checkmate.global.component.EmailSender;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventAttendance.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.global.component.ExcelGenerator;
import checkmate.com.checkmate.global.component.ExcelReader;
import checkmate.com.checkmate.global.component.PdfGenerator;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.user.domain.User;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.Even;
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
public class EventAttendanceListService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceRepository eventAttendanceRepository;
    @Autowired
    private final ExcelReader excelReader;
    @Autowired
    private final S3Uploader s3Uploader;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PdfGenerator pdfGenerator;
    @Autowired
    private final EmailSender emailSender;
    @Autowired
    private final ExcelGenerator excelGenerator;

    @Transactional
    public StudentInfoResponseDto getStudentInfoByStudentNumber(Long userId, Long eventId, int studentId, String eventDate) throws StudentAlreadyAttendedException {
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        if (event == null) {
            throw new GeneralException(EVENT_NOT_FOUND);
        } else {
            String eventTitle = event.getEventTitle();
            Long eventScheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);
            EventAttendance studentInfoFromEventAttendance = eventAttendanceRepository.findByEventScheduleIdAndStudentNumber(eventScheduleId, studentId);
            if (studentInfoFromEventAttendance == null) {
                throw new GeneralException(STUDENT_NOT_FOUND);
            } else if (!studentInfoFromEventAttendance.getSign().isEmpty()) {
                throw new GeneralException(STUDENT_ALREADY_CHECK);
            } else {
                return StudentInfoResponseDto.of(studentInfoFromEventAttendance, eventTitle, studentInfoFromEventAttendance.getName());
            }
        }
    }

    @Transactional
    public List<StudentInfoResponseDto> getStudentInfoByPhoneNumberSuffix(Long userId, Long eventId, String phoneNumberSuffix, String eventDate) throws StudentAlreadyAttendedException {
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        if (event == null) {
            throw new GeneralException(EVENT_NOT_FOUND);
        }

        String eventTitle = event.getEventTitle();
        Long eventScheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);

        List<EventAttendance> studentInfosFromEventAttendance = eventAttendanceRepository.findAllByEventScheduleIdAndPhoneNumberSuffix(eventScheduleId, phoneNumberSuffix);

        if (studentInfosFromEventAttendance == null || studentInfosFromEventAttendance.isEmpty()) {
            throw new GeneralException(STUDENT_NOT_FOUND);
        }

        // 출석한 학생 제거
        studentInfosFromEventAttendance.removeIf(EventAttendance::isAttendance);

        // 모든 학생이 이미 출석했다면 예외 발생
        if (studentInfosFromEventAttendance.isEmpty()) {
            throw new StudentAlreadyAttendedException("STUDENT_ALREADY_CHECK");
        }

        List<StudentInfoResponseDto> responseList = new ArrayList<>();
        for (EventAttendance studentInfo : studentInfosFromEventAttendance) {
            // 이름 가운데 글자를 'O'로 변경
            String maskedName = maskMiddleName(studentInfo.getStudentName());

            // 변경된 이름을 사용하여 DTO 생성
            responseList.add(StudentInfoResponseDto.of(studentInfo, eventTitle, maskedName));
        }

        return responseList;
    }


    @Transactional
    public void postSign(Long userId, Long eventId, Long studentInfoId, MultipartFile signImage) {
        EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(studentInfoId);
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        int numOfEvents = event.getEventSchedules().size();
        if (eventAttendance == null)
            throw new GeneralException(STUDENT_NOT_FOUND);
        String imageUrl = null;
        if (signImage != null) {
            imageUrl = s3Uploader.saveFile(signImage, String.valueOf(userId), "event/" + String.valueOf(eventId) + "/sign");
            eventAttendance.updateAttendanceByAttendanceCheck(imageUrl, numOfEvents);
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
    public List<String> downloadAttendanceList(Long userId, Long eventId) throws IOException {
        List<String> filenames = new ArrayList<>();
        User user = userRepository.findByUserId(userId);
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        String eventTitle = event.getEventTitle();
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        MultipartFile attendanceListEachMultipartFile = pdfGenerator.generateEventAttendanceListPdf(eventTitle, eventSchedules);
        int completion = event.getMinCompletionTimes();
        MultipartFile attendanceListTotalMultipartFile = excelGenerator.generateExcel(eventTitle, eventSchedules, completion);
        List<MultipartFile> files = new ArrayList<>();
        files.add(attendanceListEachMultipartFile);
        files.add(attendanceListTotalMultipartFile);
        String originalFilename = attendanceListEachMultipartFile.getOriginalFilename();
        String attendanceListEachUrl = s3Uploader.saveFile(attendanceListEachMultipartFile, String.valueOf(userId), "event/" + String.valueOf(event.getEventId()));
        event.updateAttendanceListFileBetweenEvent(attendanceListEachUrl);
        filenames.add(attendanceListEachUrl);
        filenames.add(originalFilename);
        return filenames;
    }

    public void sendAttendanceList(Long userId, Long eventId) throws IOException {
        User user = userRepository.findByUserId(userId);
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        String eventTitle = event.getEventTitle();
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        MultipartFile attendanceListEachMultipartFile = null;
        MultipartFile attendanceListTotalMultipartFile = null;
        if(event.getEventTarget() == EventTarget.EXTERNAL) {
            System.out.println("외부행사임ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");
            attendanceListEachMultipartFile = pdfGenerator.generateEventAttendanceListPdfAboutExternalEvent(eventTitle, eventSchedules);
            int completion = event.getMinCompletionTimes();
            attendanceListTotalMultipartFile = excelGenerator.generateExcelAboutExternalEvent(eventTitle, eventSchedules, completion);
        }
        else {
            attendanceListEachMultipartFile = pdfGenerator.generateEventAttendanceListPdf(eventTitle, eventSchedules);
            int completion = event.getMinCompletionTimes();
            attendanceListTotalMultipartFile = excelGenerator.generateExcel(eventTitle, eventSchedules, completion);
        }
        List<MultipartFile> files = new ArrayList<>();
        files.add(attendanceListEachMultipartFile);
        files.add(attendanceListTotalMultipartFile);
        emailSender.sendEmailWithFile(user, event, files);
        String attendanceListEachUrl = s3Uploader.saveFile(attendanceListEachMultipartFile, String.valueOf(userId), "event/" + String.valueOf(event.getEventId()));
        String attendanceTotalListUrl = s3Uploader.saveFile(attendanceListTotalMultipartFile, String.valueOf(userId), "event/" + String.valueOf(event.getEventId()));
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
    public List<EventAttendanceListResponseDto> updateAttendanceList(Long userId, Long eventId, List<EventAttendanceListRequestDto> eventAttendanceListRequestDtos) {
        // eventAttendanceListId가 userId, eventId랑 다 맞는지 확인
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        int numOfEvents = event.getEventSchedules().size();
        List<EventAttendanceListResponseDto> eventAttendanceListResponseDtos = new ArrayList<>();

        for (EventAttendanceListRequestDto eventAttendanceListrequestDto : eventAttendanceListRequestDtos) {
            EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceListId(eventAttendanceListrequestDto.getStudentInfoId());
            eventAttendance.updateAttendanceByManager(eventAttendanceListrequestDto.getAttendace(), numOfEvents);
            eventAttendanceRepository.save(eventAttendance);
            eventAttendanceListResponseDtos.add(EventAttendanceListResponseDto.of(eventAttendance));
        }

        return eventAttendanceListResponseDtos;
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
