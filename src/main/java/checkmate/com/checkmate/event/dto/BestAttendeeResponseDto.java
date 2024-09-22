package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.student.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Total Statistic About Event Response")
public class BestAttendeeResponseDto {

    @Schema(description = "학생 ID", example = "1")
    private Long studentId;

    @Schema(description = "학생 이름", example = "김눈송")
    private String studentName;

    @Schema(description = "학생 학번", example = "1234567")
    private int studentNumber;

    @Schema(description = "학생 학과", example = "컴퓨터과학전공")
    private String studentMajor;

    @Schema(description = "학생 메일주소", example = "checkmatesmwu@gmail.com")
    private String studentEmail;

    @Schema(description = "학생 출석률", example = "90.7")
    private float attendanceRate;

    @Schema(description = "학생 출석횟수", example = "14")
    private int attendanceTime;

    public static BestAttendeeResponseDto of(Student student) {
        return BestAttendeeResponseDto.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .studentNumber(student.getStudentNumber())
                .studentMajor(student.getStudentMajor())
                .studentEmail(student.getStudentEmail())
                .attendanceRate(student.getAttendanceRate())
                .attendanceTime(student.getAttendanceTime())
                .build();
    }
}
