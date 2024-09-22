package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.student.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event Statistic Response")
public class EventStatisticResponseDto {

    @Schema(description = "행사 일정", example = "2024-04-12, 2024-04-13")
    private final List<String> eventDates;

    @Schema(description = "행사 세부통계 정보")
    private final List<EventStatisticDetailResponseDto> eventStatisticDetailResponseDtos;

    public static EventStatisticResponseDto of(List<String> eventDates, List<Student> students, boolean completion){
        List<EventStatisticDetailResponseDto> eventStatisticDetailResponseDtos = students.stream()
                .map(student -> EventStatisticDetailResponseDto.of(student, completion))
                .collect(Collectors.toList());
        return new EventStatisticResponseDto(
                eventDates,
                eventStatisticDetailResponseDtos
        );
    }
}
