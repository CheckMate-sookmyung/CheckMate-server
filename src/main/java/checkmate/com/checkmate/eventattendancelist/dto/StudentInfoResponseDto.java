package checkmate.com.checkmate.eventattendancelist.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventattendancelist.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendancelist.service.EventAttendanceListService;
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

    public static StudentInfoResponseDto of(EventAttendanceList eventAttendanceList, String eventTitle){
        return new StudentInfoResponseDto(
                eventAttendanceList.getEventAttendanceListId(),
                eventTitle,
                eventAttendanceList.getName(),
                eventAttendanceList.getStudentNumber(),
                eventAttendanceList.getMajor()
        );
    }
}



