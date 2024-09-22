package checkmate.com.checkmate.eventAttendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Schema(description = "Attendance Update Request")
public class AttendanceUpdateRequestDto {

    @Schema(description = "참석자 ID", example = "1")
    private final Long attendeeId;

    @Schema(description = "참석자 참석 여부", example = "true")
    private final Boolean attendance;
}
