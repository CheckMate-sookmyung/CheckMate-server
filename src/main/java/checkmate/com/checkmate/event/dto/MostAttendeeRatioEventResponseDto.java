package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Total Statistic About Student Response")
public class MostAttendeeRatioEventResponseDto {

    @Schema(description = "행사 ID", example = "1")
    private Long eventId;

    @Schema(description = "행사 제목", example = "제목")
    private String eventTitle;

    @Schema(description = "행사 출석률", example = "82.7")
    private double eventRating;

    public static MostAttendeeRatioEventResponseDto of(Event event){
        return MostAttendeeRatioEventResponseDto.builder()
                .eventId(event.getEventId())
                .eventTitle(event.getEventTitle())
                .eventRating(event.getEventAttendanceRatio())
                .build();
    }
}
