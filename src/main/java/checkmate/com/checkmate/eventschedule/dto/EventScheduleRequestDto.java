package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventScheduleRequestDto {
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;

    public EventSchedule toEntity(){
        return EventSchedule.builder()
                .eventDate(eventDate)
                .eventStartTime(eventStartTime)
                .eventEndTime(eventEndTime)
                .build();
    }
}


