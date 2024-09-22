package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StudentEventAttendanceResponseDto {
    private final Long attendeeId;
    private final String attendeeName;
    private final int studentNumber;
    private final String attendeeAffiliation;
    private final String attendeePhoneNumber;
    private final String attendeeEmail;
    private final boolean attendance;
    private final String sign;
    private final LocalDateTime attendTime;

    public static StudentEventAttendanceResponseDto of(EventAttendance eventAttendance){
        return new StudentEventAttendanceResponseDto(
                eventAttendance.getEventAttendanceId(),
                eventAttendance.getStudent().getStudentName(),
                eventAttendance.getStudent().getStudentNumber(),
                eventAttendance.getStudent().getStudentMajor(),
                eventAttendance.getStudent().getStudentPhoneNumber(),
                eventAttendance.getStudent().getStudentEmail(),
                eventAttendance.isAttendance(),
                eventAttendance.getSign(),
                eventAttendance.getAttendTime());
    }

}
