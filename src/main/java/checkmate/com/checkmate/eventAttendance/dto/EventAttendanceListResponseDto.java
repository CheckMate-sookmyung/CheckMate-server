package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventAttendanceListResponseDto {
    private final Long studentInfoId;
    private final String studentName;
    private final int studentNumber;
    private final String major;
    private final String phoneNumber;
    private final String email;
    private final boolean attendance;
    private final String sign;
    private final LocalDateTime createdDate;

    public static EventAttendanceListResponseDto of(EventAttendance eventAttendance){
        return new EventAttendanceListResponseDto(
                eventAttendance.getEventAttendanceListId(),
                eventAttendance.getName(),
                eventAttendance.getStudentNumber(),
                eventAttendance.getMajor(),
                eventAttendance.getPhoneNumber(),
                eventAttendance.getEmail(),
                eventAttendance.isAttendance(),
                eventAttendance.getSign(),
                eventAttendance.getCreatedDate());
    }

}
