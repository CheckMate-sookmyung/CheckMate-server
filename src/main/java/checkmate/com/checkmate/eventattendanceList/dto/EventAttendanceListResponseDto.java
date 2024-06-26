package checkmate.com.checkmate.eventattendanceList.dto;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventAttendanceListResponseDto {
    private final String studentName;
    private final int studentNumber;
    private final String major;
    private final String phoneNumber;
    private final String email;
    private final boolean attendance;
    private final String sign;
    private final LocalDateTime createdDate;

    public static EventAttendanceListResponseDto of(EventAttendanceList eventAttendanceList){
        return new EventAttendanceListResponseDto(
                eventAttendanceList.getName(),
                eventAttendanceList.getStudentNumber(),
                eventAttendanceList.getMajor(),
                eventAttendanceList.getPhoneNumber(),
                eventAttendanceList.getEmail(),
                eventAttendanceList.isAttendance(),
                eventAttendanceList.getSign(),
                eventAttendanceList.getCreatedDate());
    }

}
