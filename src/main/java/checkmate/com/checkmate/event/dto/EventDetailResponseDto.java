package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventDetailResponseDto {
    private final Long eventId;
    private final String eventTitle;
    private final String eventDetail;
    private final String eventImage;
    private final List<EventScheduleResponseDto> eventSchedules;
    private final boolean alarmResponse;
    public static EventDetailResponseDto of(Event event) {
        List<EventScheduleResponseDto> EventSchedulesDto = event.getEventSchedules().stream()
                .map(EventScheduleResponseDto::of)
                .collect(Collectors.toList());
        return new EventDetailResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getEventDetail(),
                event.getEventImage(),
                EventSchedulesDto,
                event.getAlarmResponse()
        );
    }
}
    

