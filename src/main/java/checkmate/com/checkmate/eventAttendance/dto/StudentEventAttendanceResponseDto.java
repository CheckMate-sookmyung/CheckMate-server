package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Student Attendance Response")
public class StudentEventAttendanceResponseDto {

    @Schema(description = "참석자 ID", example = "1")
    private final Long attendeeId;

    @Schema(description = "참석자 이름", example = "김눈송")
    private final String attendeeName;

    @Schema(description = "참석자 학번", example = "1234567")
    private final int studentNumber;

    @Schema(description = "참석자 학과", example = "컴퓨터과학전공")
    private final String attendeeAffiliation;

    @Schema(description = "참석자 전화번호", example = "010-5336-5708")
    private final String attendeePhoneNumber;

    @Schema(description = "참석자 메일주소", example = "checkmatesmwu@gmail.com")
    private final String attendeeEmail;

    @Schema(description = "참석자 참석여부", example = "true")
    private final boolean attendance;

    @Schema(description = "참석자 서명 사진 주소", example = "https://aws.com")
    private final String sign;

    @Schema(description = "참석자 참석 시간", example = "2024-09-22T15:45:12.123456")
    private final LocalDateTime attendTime;

    public static StudentEventAttendanceResponseDto of(EventAttendance eventAttendance){
        return new StudentEventAttendanceResponseDto(
                eventAttendance.getEventAttendanceId(),
                eventAttendance.getStudent().getStudentName(),
                eventAttendance.getStudent().getStudentNumber(),
                eventAttendance.getStudent().getStudentMajor(),
                eventAttendance.getStudent().getStudentPhoneNumber(),
                eventAttendance.getStudent().getStudentEmail(),
                eventAttendance.isAttendance(),
                eventAttendance.getSign(),
                eventAttendance.getAttendTime());
    }

}
