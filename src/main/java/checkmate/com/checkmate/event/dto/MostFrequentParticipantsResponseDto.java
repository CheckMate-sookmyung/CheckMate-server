package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.student.domain.Student;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MostFrequentParticipantsResponseDto {
    private Long studentId;
    private String studentName;
    private int studentNumber;
    private String studentMajor;
    private String studentEmail;
    private float attendanceRate;
    private int attendanceTime;

    public static MostFrequentParticipantsResponseDto of(Student student) {
        return MostFrequentParticipantsResponseDto.builder()
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
