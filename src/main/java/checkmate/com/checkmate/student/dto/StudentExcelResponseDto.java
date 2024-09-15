package checkmate.com.checkmate.student.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StudentExcelResponseDto {
    private final String studentName;
    private final int studentNumber;
    private final String studentMajor;
    private final String studentPhoneNumber;
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
