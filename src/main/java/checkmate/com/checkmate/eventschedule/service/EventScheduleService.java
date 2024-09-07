package checkmate.com.checkmate.eventschedule.service;

import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventScheduleService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceService eventAttendanceService;

/*    @Transactional
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
    }*/
}
