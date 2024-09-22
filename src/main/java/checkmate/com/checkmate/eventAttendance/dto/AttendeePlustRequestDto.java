package checkmate.com.checkmate.eventAttendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@Schema(description = "Attendee Plus Request")
public class AttendeePlustRequestDto {

    @Schema(description = "참석자 이름", example = "김눈송")
    private String attendeeName;

    @Schema(description = "참석자 학번 (외부 행사일 경우 null)", example = "1234567")
    private int attendeeStudentNumber;

    @Schema(description = "참석자 소속/학과", example = "컴퓨터과학전공")
    private String attendeeAffiliation;

    @Schema(description = "참석자 전화번호", example = "010-5336-5708")
    private String attendeePhoneNumber;

    @Schema(description = "참석자 메일주소", example = "checkmatesmwu@gmail.com")
    private String attendeeEmail;
}
