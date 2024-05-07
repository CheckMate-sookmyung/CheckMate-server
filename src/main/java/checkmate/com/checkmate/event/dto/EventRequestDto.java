package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventRequestDto {
    private final String eventTitle;
    private final String eventDetail;
    private final List<EventScheduleRequestDto> eventSchedules;
    private final String eventImage;
    private final Boolean alarmRequest;

    public Event toEntity(List<EventSchedule> eventSchedules){
        return Event.builder()
                .eventTitle(eventTitle)
                .eventDetail(eventDetail)
                .eventSchedules(eventSchedules)
                .eventImage(eventImage)
                .alarmRequest(alarmRequest)
                .build();
    }
}
