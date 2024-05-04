package checkmate.com.checkmate.eventschedule.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    
}
