package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.global.domain.EventTarget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class EventAttendanceDetailRequestDto {
    private String attendeeName;
    private int attendeeStudentNumber;
    private String attendeeAffiliation;
    private String attendeePhoneNumber;
    private String attendeeEmail;
}
