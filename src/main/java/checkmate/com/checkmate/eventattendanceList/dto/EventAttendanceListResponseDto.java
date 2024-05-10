package checkmate.com.checkmate.eventattendanceList.dto;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventAttendanceListResponseDto {
    private final String studentName;
    private final int studentNumber;
    private final String major;
    private final boolean attendance;
    private final String sign;

    public static EventAttendanceListResponseDto of(EventAttendanceList eventAttendanceList){
        return new EventAttendanceListResponseDto(
                eventAttendanceList.getName(),
                eventAttendanceList.getStudentNumber(),
                eventAttendanceList.getMajor(),
                eventAttendanceList.isAttendance(),
                eventAttendanceList.getSign());
    }

}
