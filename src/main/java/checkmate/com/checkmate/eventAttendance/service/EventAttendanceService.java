package checkmate.com.checkmate.eventAttendance.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.*;
import checkmate.com.checkmate.eventschedule.dto.StrangerEventScheduleResponseDto;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.component.*;
import checkmate.com.checkmate.global.domain.CsvResultDto;
import checkmate.com.checkmate.mail.component.EmailSender;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import checkmate.com.checkmate.stranger.domain.Stranger;
import checkmate.com.checkmate.stranger.domain.repository.StrangerRepository;
import checkmate.com.checkmate.stranger.dto.StrangerExcelResponseDto;
import checkmate.com.checkmate.student.domain.Student;
import checkmate.com.checkmate.student.domain.repository.StudentRepository;
import checkmate.com.checkmate.student.dto.StudentExcelResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final StudentRepository studentRepository;
    @Autowired
    private final StrangerRepository strangerRepository;
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
    @Autowired
    private CsvReader csvReader;
    private final WorkbookToMultipartFileConverter workbookToMultipartFileConverter;

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
            } else if (!(studentInfoFromEventAttendance.getSign()==null)) {
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
            if(event.getEventTarget() == EventTarget.INTERNAL)
                eventAttendance.getStudent().updateAttendance();
        } else
            throw new GeneralException(IMAGE_IS_NULL);
    }

    @Transactional
    public List<EventAttendance> readAttendanceList(Member member, MultipartFile attendanceListFile, EventSchedule eventSchedule, EventTarget eventTarget) throws IOException {
        List<EventAttendance> eventAttendances = new ArrayList<>();
        List<StudentExcelResponseDto> studentExcelResponseDtos = new ArrayList<>();
        List<StrangerExcelResponseDto> strangerExcelResponseDtos = new ArrayList<>();
        try {
            if (eventTarget == EventTarget.INTERNAL) {
                studentExcelResponseDtos = excelReader.readAttendanceListAboutStudent(convertMultiPartToFile(attendanceListFile));
                eventAttendances = saveStudentAttendanceList(member, studentExcelResponseDtos, eventSchedule);
            } else {
                strangerExcelResponseDtos = excelReader.readAndSaveAttendanceListAboutStranger(convertMultiPartToFile(attendanceListFile));
                eventAttendances = saveStrangerAttendanceList(member, strangerExcelResponseDtos, eventSchedule);
            }
        } catch (IOException e) {
            throw new GeneralException(IO_EXCEPTION);
        }
        return eventAttendances;
    }

    @Transactional
    public List<EventAttendance> saveStudentAttendanceList(Member member, List<StudentExcelResponseDto> studentExcelResponseDtos, EventSchedule eventSchedule){
        List<EventAttendance> eventAttendances = new ArrayList<>();
        System.out.println(studentExcelResponseDtos.get(0).getStudentName());
        for(StudentExcelResponseDto excelResponseDto : studentExcelResponseDtos) {
            Optional<Student> studentOpt = studentRepository.findByStudentNumberAndMemberId(excelResponseDto.getStudentNumber(), member.getMemberId());
            Student attendanceStudent;

            if (studentOpt.isPresent()) {
                attendanceStudent = studentOpt.get();
                attendanceStudent.updateApplication();
            } else {
                attendanceStudent = Student.builder()
                        .studentName(excelResponseDto.getStudentName())
                        .studentNumber(excelResponseDto.getStudentNumber())
                        .studentMajor(excelResponseDto.getStudentMajor())
                        .studentPhoneNumber(excelResponseDto.getStudentPhoneNumber())
                        .studentEmail(excelResponseDto.getStudentEmail())
                        .member(member)
                        .build();
                studentRepository.save(attendanceStudent);
            }

            EventAttendance eventAttendance = EventAttendance.builder()
                    .eventSchedule(eventSchedule)
                    .student(attendanceStudent)
                    .attendance(false)
                    .build();

            eventAttendanceRepository.save(eventAttendance);
            eventAttendances.add(eventAttendance);
        }
        return eventAttendances;
    }

    @Transactional
    public List<EventAttendance> saveStrangerAttendanceList(Member member, List<StrangerExcelResponseDto> strangerExcelResponseDtos, EventSchedule eventSchedule) {
        List<EventAttendance> eventAttendances = new ArrayList<>();
        for(StrangerExcelResponseDto strangerExcelResponseDto : strangerExcelResponseDtos) {
            Optional<Stranger> strangerOpt = strangerRepository.findByStrangerPhoneNumberAndStrangerNameAndMemberMemberId(strangerExcelResponseDto.getStrangerPhoneNumber(), strangerExcelResponseDto.getStrangerName(), member.getMemberId());
            Stranger attendanceStranger;

            if (strangerOpt.isPresent()) {
                attendanceStranger = strangerOpt.get();
            } else {
                attendanceStranger = Stranger.builder()
                        .strangerName(strangerExcelResponseDto.getStrangerName())
                        .strangerPhoneNumber(strangerExcelResponseDto.getStrangerPhoneNumber())
                        .strangerEmail(strangerExcelResponseDto.getStrangerEmail())
                        .strangerAffiliation(strangerExcelResponseDto.getStrangerAffiliation())
                        .member(member)
                        .build();
                strangerRepository.save(attendanceStranger);
            }

            EventAttendance eventAttendance = EventAttendance.builder()
                    .eventSchedule(eventSchedule)
                    .stranger(attendanceStranger)
                    .attendance(false)
                    .build();

            eventAttendanceRepository.save(eventAttendance);
            eventAttendances.add(eventAttendance);
        }
        return eventAttendances;
    }




        @Transactional
    public AttendanceListFileUrlResponseDto downloadAttendanceList(Accessor accessor, Long eventId) throws IOException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        //List<String> filenames = new ArrayList<>();
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
        String fileUrl = attendanceListEachUrl;
        //filenames.add(originalFilename);
        return AttendanceListFileUrlResponseDto.of(fileUrl);
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
    public List<Object> updateAttendanceList(Accessor accessor, Long eventId, List<AttendanceUpdateRequestDto> attendanceUpdateRequestDtos) {
        // eventAttendanceListId가 userId, eventId랑 다 맞는지 확인
        Event event = eventRepository.findByMemberIdAndEventId(accessor.getMemberId(), eventId);
        List<Object> eventAttendanceResponseDtos = new ArrayList<>();

        if (event.getEventTarget() == EventTarget.INTERNAL) {
            List<?> studentEventAttendanceResponseDtos = new ArrayList<>();
            for (AttendanceUpdateRequestDto eventAttendanceListrequestDto : attendanceUpdateRequestDtos) {
                EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(eventAttendanceListrequestDto.getAttendeeId());
                eventAttendance.updateAttendanceByManager(eventAttendanceListrequestDto.getAttendance());
                eventAttendanceRepository.save(eventAttendance);
                eventAttendanceResponseDtos.add(StudentEventAttendanceResponseDto.of(eventAttendance));
            }
        }
        else{
            List<?> strangerEventAttendanceResponseDtos = new ArrayList<>();
            for (AttendanceUpdateRequestDto eventAttendanceListrequestDto : attendanceUpdateRequestDtos) {
                EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(eventAttendanceListrequestDto.getAttendeeId());
                eventAttendance.updateAttendanceByManager(eventAttendanceListrequestDto.getAttendance());
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

    public void deleteAttendanceList(Accessor accessor, Long eventId, Long eventScheduleId, Long attendaeeId) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        EventAttendance eventAttendance = eventAttendanceRepository.findByEventAttendanceId(attendaeeId);
        eventAttendanceRepository.delete(eventAttendance);
    }

    public void addAttendee(Accessor accessor, Long eventId, Long eventScheduleId, List<AttendeePlustRequestDto> attendeePlustRequestDtos) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        EventSchedule eventSchedule = eventScheduleRepository.findEventScheduleByEventScheduleId(eventScheduleId);
        if(event.getEventTarget() == EventTarget.INTERNAL) {
            List<StudentExcelResponseDto> studentExcelResponseDtos = null;
            for(AttendeePlustRequestDto attendeePlustRequestDto : attendeePlustRequestDtos){
                studentExcelResponseDtos.add(StudentExcelResponseDto.of(attendeePlustRequestDto.getAttendeeName(),
                        attendeePlustRequestDto.getAttendeeStudentNumber(),
                        attendeePlustRequestDto.getAttendeeAffiliation(),
                        attendeePlustRequestDto.getAttendeePhoneNumber(),
                        attendeePlustRequestDto.getAttendeeEmail()));
            }
            saveStudentAttendanceList(loginMember, studentExcelResponseDtos, eventSchedule);
        }
        else{
            List<StrangerExcelResponseDto> strangerExcelResponseDtos = null;
            for(AttendeePlustRequestDto attendeePlustRequestDto : attendeePlustRequestDtos){
                strangerExcelResponseDtos.add(StrangerExcelResponseDto.of(attendeePlustRequestDto.getAttendeeName(),
                        attendeePlustRequestDto.getAttendeePhoneNumber(),
                        attendeePlustRequestDto.getAttendeeEmail(),
                        attendeePlustRequestDto.getAttendeeAffiliation()));
            }
            saveStrangerAttendanceList(loginMember, strangerExcelResponseDtos, eventSchedule);
        }
    }

    public String uploadAttendanceListAboutOnline(Accessor accessor, Long eventScheduleId, MultipartFile attendanceFile) throws IOException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        EventSchedule eventSchedule = eventScheduleRepository.findEventScheduleByEventScheduleId(eventScheduleId);
        Event event = eventSchedule.getEvent();
        List<EventAttendance> attendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
        CsvResultDto csvResultDto = csvReader.parseCsvFile(attendanceFile);
        Map<Integer, Integer> studentAttendanceList = csvResultDto.getStudentTimeMap();

        for (EventAttendance attendance : attendances) {
            int studentNumber = attendance.getStudent().getStudentNumber();

            if (studentAttendanceList.containsKey(studentNumber)) {
                int csvAttendanceTime = studentAttendanceList.get(studentNumber);

                if (csvAttendanceTime >= event.getCompletionTime()) {
                    attendance.updateAttendanceAboutOnlineEvent(true, csvAttendanceTime);
                } else {
                    attendance.updateAttendanceAboutOnlineEvent(false, csvAttendanceTime);
                }
                eventAttendanceRepository.save(attendance);
            }
        }

        Workbook workbook = excelGenerator.generateOnlineAttendaceExcel(attendances, csvResultDto.getFailedTimeMap());
        MultipartFile onlineAttendanceFile = workbookToMultipartFileConverter.convert(workbook, event.getEventTitle() +"_"+eventSchedule.getEventDate()+"_온라인_출석명단" + ".xlsx");
        String onlineAttendanceListUrl = s3Uploader.saveFile(onlineAttendanceFile, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(event.getEventId()));
        event.updateAttendanceListFile(null, onlineAttendanceListUrl);
        return onlineAttendanceListUrl;
    }

    @Transactional
    public List<Object> getAttendanceList(Accessor accessor, Long eventId){
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        List<Object> eventScheduleResponseDtos = new ArrayList<>();
        if (eventSchedules.isEmpty())
            throw new GeneralException(EVENT_NOT_FOUND);
        else {
            if(event.getEventTarget() == EventTarget.INTERNAL) {
                for (EventSchedule eventSchedule : eventSchedules) {
                    Long eventScheduleId = eventSchedule.getEventScheduleId();
                    List<EventAttendance> eventAttendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
                    eventScheduleResponseDtos.add(StudentEventScheduleResponseDto.of(eventSchedule, eventAttendances));
                }
            }
            else{
                for (EventSchedule eventSchedule : eventSchedules) {
                    Long eventScheduleId = eventSchedule.getEventScheduleId();
                    List<EventAttendance> eventAttendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
                    eventScheduleResponseDtos.add(StrangerEventScheduleResponseDto.of(eventSchedule, eventAttendances));
                }
            }
            return eventScheduleResponseDtos;

        }
    }
}
