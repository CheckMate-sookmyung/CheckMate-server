package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventattendanceList.dto.EventAttendanceListResponseDto;
import checkmate.com.checkmate.eventattendanceList.dto.StudentInfoResponseDto;
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
    private final List<EventAttendanceListResponseDto> eventAttendanceListResponseDtos;

    public static EventScheduleResponseDto of(EventSchedule eventSchedule){
        List<EventAttendanceListResponseDto> eventAttendanceListResponses = eventSchedule.getEventAttendanceLists().stream()
                .map(EventAttendanceListResponseDto::of)
                .collect(Collectors.toList());
        return new EventScheduleResponseDto(
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime(),
                eventAttendanceListResponses
        );
    }
}
