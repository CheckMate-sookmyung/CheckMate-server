package checkmate.com.checkmate.eventschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventScheduleRequestDto {
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;
}
