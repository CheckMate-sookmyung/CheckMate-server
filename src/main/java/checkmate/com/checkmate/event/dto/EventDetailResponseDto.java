package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.domain.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event Detail Response")
public class EventDetailResponseDto {
    private final Long eventId;
    private final String eventTitle;
    private final String eventDetail;
    private final String eventImage;
    private final EventType eventType;
    private final EventTarget eventTarget;
    private final List<EventScheduleResponseDto> eventSchedules;
    private final boolean alaramRequest;
    private final boolean alarmResponse;
    private final String managerName;
    private final String managerPhoneNumber;
    private final String managerEmail;
    public static EventDetailResponseDto of(Event event, List<EventSchedule> eventSchedules) {
        List<EventScheduleResponseDto> EventSchedulesDto = eventSchedules.stream()
                .map(EventScheduleResponseDto::of)
                .collect(Collectors.toList());
        return new EventDetailResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getEventDetail(),
                event.getEventImage(),
                event.getEventType(),
                event.getEventTarget(),
                EventSchedulesDto,
                event.getAlarmRequest(),
                event.getAlarmResponse(),
                event.getManagerName(),
                event.getManagerPhoneNumber(),
                event.getManagerEmail()
        );
    }
}
    

