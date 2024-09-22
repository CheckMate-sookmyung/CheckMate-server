package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Stranger Attendee Info Response")
public class StrangerInfoResponseDto {

    @Schema(description = "참석자 ID", example = "1")
    private final Long attendeeId;

    @Schema(description = "행사 제목", example = "제목")
    private final String eventTitle;

    @Schema(description = "참석자 이름", example = "김눈송")
    private final String attendeeName;

    @Schema(description = "참석자 전화번호", example = "010-5336-5708")
    private final String attendeePhoneNumber;

    @Schema(description = "참석자 소속", example = "LG")
    private final String attendeeAffiliation;

    public static StrangerInfoResponseDto of(EventAttendance eventAttendance, String eventTitle, String maskedName) {
        return new StrangerInfoResponseDto(
                eventAttendance.getEventAttendanceId(),
                eventTitle,
                maskedName,
                eventAttendance.getStranger().getStrangerPhoneNumber(),
                eventAttendance.getStranger().getStrangerAffiliation()
        );
    }
}
