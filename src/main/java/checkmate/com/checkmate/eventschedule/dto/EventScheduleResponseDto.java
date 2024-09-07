package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.dto.EventAttendanceResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.collect;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventScheduleResponseDto {
    private final String eventDate;
    private final String eventStartTime;
    private final String eventEndTime;
    private final List<EventAttendanceResponseDto> attendanceListResponseDtos;

    public static EventScheduleResponseDto of(EventSchedule eventSchedule, List<EventAttendance> eventAttendances){
        List<EventAttendanceResponseDto> eventAttendanceListResponses = eventAttendances.stream()
                .map(EventAttendanceResponseDto::of)
                .collect(Collectors.toList());
        return new EventScheduleResponseDto(
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime(),
                eventAttendanceListResponses
        );
    }
}
