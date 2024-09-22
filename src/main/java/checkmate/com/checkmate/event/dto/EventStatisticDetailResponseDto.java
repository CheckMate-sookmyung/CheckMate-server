package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.student.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event Statistic Detail Response")
public class EventStatisticDetailResponseDto {

    @Schema(description = "학번", example = "1234567")
    private final int studentNumber;

    @Schema(description = "학과", example = "컴퓨터과학전공")
    private final String studentMajor;

    @Schema(description = "이수 여부", example = "true")
    private final Boolean completion;

    public static EventStatisticDetailResponseDto of(Student student, boolean completion){
        return new EventStatisticDetailResponseDto(
                student.getStudentNumber(),
                student.getStudentMajor(),
                completion
        );
    }
}
