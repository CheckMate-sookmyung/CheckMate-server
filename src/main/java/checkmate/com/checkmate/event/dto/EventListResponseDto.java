package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EventListener;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class EventListResponseDto {
    private final Long eventId;
    private final String eventTitle;
    private final String eventImage;

    public static EventListResponseDto of(Event event){
        return new EventListResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getEventImage());
    }
}
