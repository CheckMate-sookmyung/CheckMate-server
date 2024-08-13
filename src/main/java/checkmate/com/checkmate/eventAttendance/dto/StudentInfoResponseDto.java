package checkmate.com.checkmate.eventAttendance.dto;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
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

    public static StudentInfoResponseDto of(EventAttendance eventAttendance, String eventTitle, String maskedName) {
        return new StudentInfoResponseDto(
                eventAttendance.getEventAttendanceListId(),
                eventTitle,
                maskedName,
                eventAttendance.getStudentNumber(),
                eventAttendance.getMajor()
        );
    }
}



