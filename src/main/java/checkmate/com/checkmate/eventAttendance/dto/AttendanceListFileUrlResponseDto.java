package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Schema(description = "Attendance List File Url Request")
public class AttendanceListFileUrlResponseDto {

    @Schema(description = "행사 출석명단 주소", example = "https://aws.com")
    private final String attendanceListFileUrl;

    public static AttendanceListFileUrlResponseDto of(String attendanceListFileUrl) {
        return new AttendanceListFileUrlResponseDto(
                attendanceListFileUrl
        );
    }
}
