package checkmate.com.checkmate.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Student Excel Response")
public class StudentExcelResponseDto {

    @Schema(description = "학생 이름", example = "김눈송")
    private final String studentName;

    @Schema(description = "학생 학번", example = "1234567")
    private final int studentNumber;

    @Schema(description = "학생 학과", example = "컴퓨터과학전공")
    private final String studentMajor;

    @Schema(description = "학생 전화번호", example = "010-5336-5708")
    private final String studentPhoneNumber;

    @Schema(description = "학생 메일주소", example = "checkmatesmwu@gmail.com")
    private final String studentEmail;

    public static StudentExcelResponseDto of(String studentName, int studentNumber, String studentMajor, String studentPhoneNumber, String studentEmail){
        return new StudentExcelResponseDto(
                studentName,
                studentNumber,
                studentMajor,
                studentPhoneNumber,
                studentEmail
        );
    }
}
