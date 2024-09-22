package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.dto.StrangerEventAttendanceResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event Schedule Response")
public class EventScheduleResponseDto {

    @Schema(description = "행사 일정 ID", example = "1")
    private final Long eventScheduleId;

    @Schema(description = "행사 날짜", example = "2024-04-12")
    private final String eventDate;

    @Schema(description = "행사 시작 시간", example = "16:00")
    private final String startTime;

    @Schema(description = "행사 종료 시간", example = "18:30")
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