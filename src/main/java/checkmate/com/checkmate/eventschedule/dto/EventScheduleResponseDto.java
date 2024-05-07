package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventScheduleResponseDto {
    private final String eventDate;
    private final String eventStartTime;
    private final String eventEndTime;

    public static EventScheduleResponseDto of(EventSchedule eventSchedule){
        return new EventScheduleResponseDto(
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime()
        );

    }
}
