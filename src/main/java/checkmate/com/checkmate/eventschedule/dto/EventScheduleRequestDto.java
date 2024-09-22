package checkmate.com.checkmate.eventschedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Event Schedule Request")
public class EventScheduleRequestDto {

    @Schema(description = "행사 날짜", example = "2024-04-12")
    private String eventDate;

    @Schema(description = "행사 시작 시간", example = "16:00")
    private String eventStartTime;

    @Schema(description = "행사 종료 시간", example = "18:30")
    private String eventEndTime;

}


