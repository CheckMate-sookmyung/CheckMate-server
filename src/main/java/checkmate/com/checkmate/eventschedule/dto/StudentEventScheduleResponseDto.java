package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.dto.StudentEventAttendanceResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Student Event Schedule Response")
public class StudentEventScheduleResponseDto {

    @Schema(description = "행사 날짜", example = "2024-04-12")
    private final String eventDate;

    @Schema(description = "행사 시작 시간", example = "16:00")
    private final String eventStartTime;

    @Schema(description = "행사 종료 시간", example = "18:30")
    private final String eventEndTime;

    @Schema(description = "학생 참석자 정보")
    private final List<StudentEventAttendanceResponseDto> attendanceListResponseDtos;

    public static StudentEventScheduleResponseDto of(EventSchedule eventSchedule, List<EventAttendance> eventAttendances){
        List<StudentEventAttendanceResponseDto> eventAttendanceListResponses = eventAttendances.stream()
                .map(StudentEventAttendanceResponseDto::of)
                .collect(Collectors.toList());
        return new StudentEventScheduleResponseDto(
                eventSchedule.getEventDate(),
                eventSchedule.getEventStartTime(),
                eventSchedule.getEventEndTime(),
                eventAttendanceListResponses
        );
    }
}
