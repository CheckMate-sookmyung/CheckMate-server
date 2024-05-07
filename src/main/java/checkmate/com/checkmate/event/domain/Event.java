package checkmate.com.checkmate.event.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Event {

    @Id @GeneratedValue
    private Long eventId;

    private String eventTitle;

    private String eventDetail;

    private String eventImage;

    private Boolean alarmRequest;

    private Boolean alarmResponse;

    @OneToMany(mappedBy="event")
    private List<EventSchedule> eventSchedules = new ArrayList<>();

    @Builder(toBuilder = true)
    public Event(final String eventTitle, String eventDetail, String eventImage, boolean alarmRequest, boolean alarmResponse, List<EventSchedule> eventSchedules){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.eventImage = eventImage;
        this.alarmRequest = alarmRequest;
        this.alarmResponse = false;
        this.eventSchedules = eventSchedules;
    }

    public void update(String eventTitle, String eventDetail, String eventImage, List<EventSchedule> eventSchedules){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.eventImage = eventImage;
        this.eventSchedules = eventSchedules;
    }

    public void updateAlarm(){
        this.alarmResponse = true;
    }

}
