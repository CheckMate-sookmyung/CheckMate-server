package checkmate.com.checkmate.eventschedule.service;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceListService;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static checkmate.com.checkmate.global.codes.ErrorCode.IO_EXCEPTION;

@Service
@RequiredArgsConstructor
public class EventScheduleService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceListService eventAttendanceListService;

    @Transactional
    public EventDetailResponseDto postEventSchedule(MultipartFile attendanceListFile, List<EventScheduleRequestDto> eventScheduleRequestDto, Long userId, Long eventId){
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        List<EventSchedule> savedEventSchedules = eventScheduleRequestDto.stream()
                .map(dto -> {
                    EventSchedule eventSchedule = EventSchedule.builder()
                            .eventDate(dto.getEventDate())
                            .eventStartTime(dto.getEventStartTime())
                            .eventEndTime(dto.getEventEndTime())
                            .event(event)
                            .build();
                    eventScheduleRepository.save(eventSchedule);
                    try {
                        List<EventAttendance> savedEventAttendances = eventAttendanceListService.readAndSaveAttendanceList(attendanceListFile, eventSchedule);
                        eventSchedule.setEventAttendances(savedEventAttendances);
                    } catch (IOException e) {
                        throw new GeneralException(IO_EXCEPTION);
                    }
                    return eventSchedule;
                })
                .collect(Collectors.toList());
        eventRepository.save(event);

        return EventDetailResponseDto.of(event);
    }
}
