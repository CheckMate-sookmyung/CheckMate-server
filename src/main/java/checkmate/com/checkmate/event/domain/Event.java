package checkmate.com.checkmate.event.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.user.domain.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Builder(toBuilder = true)
    public Event(final String eventTitle, String eventDetail, String eventImage, boolean alarmRequest, List<EventSchedule> eventSchedules,User user){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.eventImage = eventImage;
        this.alarmRequest = alarmRequest;
        this.alarmResponse = false;
        this.eventSchedules = eventSchedules;
        this.user = user;
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
