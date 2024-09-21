package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StrangerEventAttendanceResponseDto {
    private final Long attendeeId;
    private final String attendeeName;
    private final String attendeeAffiliation;
    private final String attendeePhoneNumber;
    private final String attendeeEmail;
    private final boolean attendance;
    private final String sign;
    private final LocalDateTime attendTime;

    public static StrangerEventAttendanceResponseDto of(EventAttendance eventAttendance){
        return new StrangerEventAttendanceResponseDto(
                eventAttendance.getEventAttendanceId(),
                eventAttendance.getStranger().getStrangerName(),
                eventAttendance.getStranger().getStrangerAffiliation(),
                eventAttendance.getStranger().getStrangerPhoneNumber(),
                eventAttendance.getStranger().getStrangerEmail(),
                eventAttendance.isAttendance(),
                eventAttendance.getSign(),
                eventAttendance.getModifiedDate());
    }
}
