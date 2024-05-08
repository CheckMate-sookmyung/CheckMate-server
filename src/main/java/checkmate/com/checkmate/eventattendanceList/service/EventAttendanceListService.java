package checkmate.com.checkmate.eventattendanceList.service;

import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendanceList.domain.repository.EventAttendanceListRepository;
import checkmate.com.checkmate.eventattendanceList.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.global.ExcelReader;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

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

    @Transactional
    public StudentInfoResponseDto getStudentInfo(Long userId, Long eventId, int studentId, String eventDate) throws StudentAlreadyAttendedException {
        String eventTitle = eventRepository.findByUserIdAndEventId(userId, eventId).getEventTitle();
        Long eventSheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);
        EventAttendanceList studentInfoFromEventAttendance =  eventAttendanceListRepository.findByEventIdAndStudentNumber(eventSheduleId, studentId);
        if (studentInfoFromEventAttendance.isAttendance())
            throw new StudentAlreadyAttendedException("이미 출석한 학생입니다.");
        else
            return StudentInfoResponseDto.of(studentInfoFromEventAttendance, eventTitle);
    }

    @Transactional
    public void postSign(Long userId, Long eventId, Long studentInfoId, MultipartFile signImage){
        EventAttendanceList eventAttendanceList = eventAttendanceListRepository.findByEventAttendanceListId(studentInfoId);
        String imageUrl = s3Uploader.saveFile(signImage, String.valueOf(userId), "event/"+String.valueOf(eventId)+"/sign");
        eventAttendanceList.updateAttendance(imageUrl);
    }

    @Async
    public Future<Void> readAndSaveAttendanceList(MultipartFile attendanceListFile, EventSchedule eventSchedule) throws IOException {
        try {
            excelReader.readAndSaveAttendanceList(eventAttendanceListRepository,convertMultiPartToFile(attendanceListFile), eventSchedule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(null);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile(file.getOriginalFilename(), null);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

}
