package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.global.domain.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event List Response")
public class EventListResponseDto {

    @Schema(description = "행사 ID", example = "1")
    private final Long eventId;

    @Schema(description = "행사 제목", example = "제목")
    private final String eventTitle;

    @Schema(description = "행사 유형", example = "ONLINE")
    private final EventType eventType;

    @Schema(description = "행사 일정", example = "2024-04-12, 2024-04-13")
    private final List<String> eventSchedules;

    @Schema(description = "행사 사진 주소", example = "https://aws.com")
    private final String eventImage;

    public static EventListResponseDto of(Event event, List<EventSchedule> eventSchedules){
        List<String> eventScheduleDate = new ArrayList<>();
        for (EventSchedule eventSchedule : eventSchedules)
            eventScheduleDate.add(eventSchedule.getEventDate());

        return new EventListResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getEventType(),
                eventScheduleDate,
                event.getEventImage());
    }
}

