package checkmate.com.checkmate.eventAttendance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class EventAttendanceRequestDto {
    private final Long studentInfoId;
    private final Boolean attendace;
}