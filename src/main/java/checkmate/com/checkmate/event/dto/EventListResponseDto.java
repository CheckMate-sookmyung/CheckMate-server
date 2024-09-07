package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventListResponseDto {
    private final Long eventId;
    private final String eventTitle;
    private final List<String> eventSchedules;
    private final String eventImage;

    public static EventListResponseDto of(Event event, List<EventSchedule> eventSchedules){
        List<String> eventScheduleDate = new ArrayList<>();
        for (EventSchedule eventSchedule : eventSchedules)
            eventScheduleDate.add(eventSchedule.getEventDate());

        return new EventListResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                eventScheduleDate,
                event.getEventImage());
    }
}

