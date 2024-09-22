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
import static org.apache.commons.collections4.CollectionUtils.collect;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Stranger Event Schedule Response")
public class StrangerEventScheduleResponseDto {

    @Schema(description = "행사 날짜", example = "2024-04-12")
    private final String eventDate;

    @Schema(description = "행사 시작 시간", example = "16:00")
    private final String eventStartTime;

    @Schema(description = "행사 종료 시간", example = "18:30")
    private final String eventEndTime;

    @Schema(description = "외부인 참석자 정보")
    private final List<StrangerEventAttendanceResponseDto> attendanceListResponseDtos;

    public static StrangerEventScheduleResponseDto of(EventSchedule eventSchedule, List<EventAttendance> eventAttendances){
        List<StrangerEventAttendanceResponseDto> eventAttendanceListResponses = eventAttendances.stream()
                .map(StrangerEventAttendanceResponseDto::of)
                .collect(Collectors.toList());
        return new StrangerEventScheduleResponseDto(
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime(),
                eventAttendanceListResponses
        );
    }
}
