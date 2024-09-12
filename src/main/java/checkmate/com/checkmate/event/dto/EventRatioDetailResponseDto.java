package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.student.domain.Student;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventRatioDetailResponseDto {
    private final int studentNumber;
    private final String studentMajor;
    private final Boolean completion;

    public static EventRatioDetailResponseDto of(Student student, boolean completion){
        return new EventRatioDetailResponseDto(
                student.getStudentNumber(),
                student.getStudentMajor(),
                completion
        );
    }
}
