package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.eventAttendance.dto.StudentEventAttendanceResponseDto;
import checkmate.com.checkmate.student.domain.Student;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

//이벤트날짜들, 학번, 학과, 이수여부
@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventRatioResponseDto {
    private final List<String> eventDates;
    private final List<EventRatioDetailResponseDto> eventRatioDetailResponseDtos;

    public static EventRatioResponseDto of(List<String> eventDates, List<Student> students, boolean completion){
        List<EventRatioDetailResponseDto> eventRatioDetailResponseDtos = students.stream()
                .map(student -> EventRatioDetailResponseDto.of(student, completion))
                .collect(Collectors.toList());
        return new EventRatioResponseDto(
                eventDates,
                eventRatioDetailResponseDtos
        );
    }
}
