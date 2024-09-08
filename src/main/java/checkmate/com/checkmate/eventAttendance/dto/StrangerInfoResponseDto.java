package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StrangerInfoResponseDto {
    private final Long strangerInfoId;
    private final String eventTitle;
    private final String strangerName;
    private final String strangerPhoneNumber;
    private final String strangerAffiliation;

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
