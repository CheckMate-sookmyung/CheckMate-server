package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.dto.StrangerEventAttendanceResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventScheduleResponseDto {

    private final Long eventScheduleId;
    private final String eventDate;
    private final String startTime;
    private final String endTime;

    public static EventScheduleResponseDto of(EventSchedule eventSchedule){
        return new EventScheduleResponseDto(
                eventSchedule.getEventScheduleId(),
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime()
        );
    }
}