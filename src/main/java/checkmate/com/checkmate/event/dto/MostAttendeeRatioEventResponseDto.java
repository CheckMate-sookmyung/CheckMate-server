package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MostAttendeeRatioEventResponseDto {
    private Long eventId;
    private String eventTitle;
    private double eventRating;

    public static MostAttendeeRatioEventResponseDto of(Event event){
        return MostAttendeeRatioEventResponseDto.builder()
                .eventId(event.getEventId())
                .eventTitle(event.getEventTitle())
                .eventRating(event.getEventAttendanceRatio())
                .build();
    }
}
