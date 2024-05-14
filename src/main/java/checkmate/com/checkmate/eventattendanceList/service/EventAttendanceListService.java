package checkmate.com.checkmate.eventattendanceList.service;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendanceList.domain.repository.EventAttendanceListRepository;
import checkmate.com.checkmate.eventattendanceList.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
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
    private final EventAttendanceListRepository eventAttendanceListRepository;
    @Autowired
    private final ExcelReader excelReader;
    @Autowired
    private final S3Uploader s3Uploader;
    @Autowired
    private final ExcelGenerator excelGenerator;
    @Autowired
    private final UserRepository userRepository;

    @Transactional
    public StudentInfoResponseDto getStudentInfo(Long userId, Long eventId, int studentId, String eventDate) throws StudentAlreadyAttendedException {
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        if (event == null)
            throw new GeneralException(EVENT_NOT_FOUND);
        else{
            String eventTitle = event.getEventTitle();
            Long eventSheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);
            EventAttendanceList studentInfoFromEventAttendance = eventAttendanceListRepository.findByEventIdAndStudentNumber(eventSheduleId, studentId);
            if (studentInfoFromEventAttendance == null)
                throw new GeneralException(STUDENT_NOT_FOUND);
            else if (studentInfoFromEventAttendance.isAttendance())
                throw new GeneralException(STUDENT_ALREADY_CHECK);
            else
                return StudentInfoResponseDto.of(studentInfoFromEventAttendance, eventTitle);
        }
    }

    @Transactional
    public void postSign(Long userId, Long eventId, Long studentInfoId, MultipartFile signImage){
        EventAttendanceList eventAttendanceList = eventAttendanceListRepository.findByEventAttendanceListId(studentInfoId);
        if (eventAttendanceList == null)
            throw new GeneralException(STUDENT_NOT_FOUND);
        String imageUrl = null;
        if (signImage != null) {
            imageUrl = s3Uploader.saveFile(signImage, String.valueOf(userId), "event/" + String.valueOf(eventId) + "/sign");
            eventAttendanceList.updateAttendance(imageUrl);
        }
        else
            throw new GeneralException(IMAGE_IS_NULL);
    }

    @Transactional
    public List<EventAttendanceList> readAndSaveAttendanceList(MultipartFile attendanceListFile, EventSchedule eventSchedule) throws IOException {
        List<EventAttendanceList> eventAttendanceLists = new ArrayList<>();
        try {
            eventAttendanceLists = excelReader.readAndSaveAttendanceList(eventAttendanceListRepository, convertMultiPartToFile(attendanceListFile), eventSchedule);
        } catch (IOException e) {
            throw new GeneralException(IO_EXCEPTION);
        }
        return eventAttendanceLists;
    }

    public void sendAttendanceList(Long userId, Long eventId) throws IOException {
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        String eventTitle = event.getEventTitle();
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        MultipartFile attendanceListMultipartFile = excelGenerator.generateExcel(eventTitle, eventSchedules);
        //MultipartFile attendanceListMultipartFile = workbookToMultipartFileConverter.convert(attendanceListWorkBook, eventTitle+"_참석자명단.xlsx");
        String attendanceListUrl = s3Uploader.saveFile(attendanceListMultipartFile, String.valueOf(userId), "event/" + String.valueOf(event.getEventId()));
        event.updateAttendanceListFileAferEvent(attendanceListUrl);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile(file.getOriginalFilename(), null);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

}
