package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Student Attendee Info Response")
public class StudentInfoResponseDto {

    @Schema(description = "참석자 ID", example = "1")
    private final Long attendeeId;

    @Schema(description = "행사 제목", example = "제목")
    private final String eventTitle;

    @Schema(description = "참석자 이름", example = "김눈송")
    private final String attendeeName;

    @Schema(description = "참석자 학번", example = "1234567")
    private final int studentNumber;

    @Schema(description = "참석자 학과", example = "컴퓨터과학전공")
    private final String attendeeAffiliation;

    public static StudentInfoResponseDto of(EventAttendance eventAttendance, String eventTitle, String maskedName) {
        return new StudentInfoResponseDto(
                eventAttendance.getEventAttendanceId(),
                eventTitle,
                maskedName,
                eventAttendance.getStudent().getStudentNumber(),
                eventAttendance.getStudent().getStudentMajor()
        );
    }
}



