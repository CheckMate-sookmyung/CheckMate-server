package checkmate.com.checkmate.eventschedule.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Time;
import java.util.Date;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class EventSchedule {

    @Id @GeneratedValue
    private Long eventScheduleId;

    private Date eventDate;

    private Time eventStartTime;

    private Time eventEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    @Builder(toBuilder=true)
    public EventSchedule(final Date eventDate, final Time eventStartTime, final Time eventEndTime, final Event event){
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.event = event;
    }

    public void update(final Date eventDate, final Time eventStartTime, final Time eventEndTime) {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
}
