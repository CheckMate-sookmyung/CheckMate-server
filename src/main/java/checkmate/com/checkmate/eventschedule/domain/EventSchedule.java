package checkmate.com.checkmate.eventschedule.domain;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class EventSchedule extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long eventScheduleId;

    @Column(nullable = false)
    private String eventDate;

    @Column(nullable = false)
    private String eventStartTime;

    @Column(nullable = false)
    private String eventEndTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="event_id")
    private Event event;

    @OneToMany(mappedBy="eventSchedule", cascade = CascadeType.ALL)
    private List<EventAttendanceList> eventAttendanceLists = new ArrayList<>();

    @Builder(toBuilder=true)
    public EventSchedule(final String eventDate, final String eventStartTime, final String eventEndTime, final Event event){
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.event = event;
    }

    public void updateEventSchedule(final String eventDate, final String eventStartTime, final String eventEndTime) {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
}
