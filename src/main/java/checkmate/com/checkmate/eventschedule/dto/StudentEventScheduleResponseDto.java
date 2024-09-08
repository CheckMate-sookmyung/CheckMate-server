package checkmate.com.checkmate.eventschedule.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.dto.StudentEventAttendanceResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StudentEventScheduleResponseDto {
    private final String eventDate;
    private final String eventStartTime;
    private final String eventEndTime;
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
