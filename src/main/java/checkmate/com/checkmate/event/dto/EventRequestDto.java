package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class EventRequestDto {
    private final String eventTitle;
    private final String eventDetail;
    private final List<EventScheduleRequestDto> eventSchedules;
    private final Boolean alarmRequest;

    public Event toEntity(User user){
        return Event.builder()
                .user(user)
                .eventTitle(eventTitle)
                .eventDetail(eventDetail)
                .build();
    }
}
