package checkmate.com.checkmate.eventattendanceList.dto;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StudentInfoResponseDto {
    private final Long studentInfoId;
    private final String eventTitle;
    private final String studentName;
    private final int studentNumber;
    private final String major;

    public static StudentInfoResponseDto of(EventAttendanceList eventAttendanceList, String eventTitle, String maskedName) {
        return new StudentInfoResponseDto(
                eventAttendanceList.getEventAttendanceListId(),
                eventTitle,
                maskedName,
                eventAttendanceList.getStudentNumber(),
                eventAttendanceList.getMajor()
        );
    }
}



