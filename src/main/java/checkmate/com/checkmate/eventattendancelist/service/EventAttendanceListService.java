package checkmate.com.checkmate.eventattendancelist.service;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.eventattendancelist.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendancelist.domain.repository.EventAttendanceListRepository;
import checkmate.com.checkmate.eventattendancelist.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventAttendanceListService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceListRepository eventAttendanceListRepository;

    @Transactional
    public StudentInfoResponseDto getStudentInfo(Long userId, Long eventId, int studentId, String eventDate){
        String eventTitle = eventRepository.findByUserIdAndEventId(userId, eventId).getEventTitle();
        Long eventSheduleId = eventScheduleRepository.findEventScheduleIdByEvent(eventId, eventDate);
        EventAttendanceList studentInfoFromEventAttendance =  eventAttendanceListRepository.findByEventIdAndStudentNumber(eventSheduleId, studentId);
        return StudentInfoResponseDto.of(studentInfoFromEventAttendance, eventTitle);
    }



}
