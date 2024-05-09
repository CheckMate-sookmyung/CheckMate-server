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

    private String eventAttendanceListFile;

    private Boolean alarmRequest;

    private Boolean alarmResponse;

    @OneToMany(mappedBy="event", cascade = CascadeType.ALL)
    private List<EventSchedule> eventSchedules = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Builder(toBuilder = true)
    public Event(final String eventTitle, String eventDetail, boolean alarmRequest,User user){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.alarmRequest = alarmRequest;
        this.alarmResponse = false;
        this.user = user;
    }

    public void update(String eventTitle, String eventDetail, String eventImage, String eventAttendanceListFile, List<EventSchedule> eventSchedules, Boolean alarmResponse){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.eventImage = eventImage;
        this.eventSchedules = eventSchedules;
        this.eventAttendanceListFile = eventAttendanceListFile;
        this.alarmRequest = alarmResponse;
    }

    public void postFileAndAttendanceList(String eventImage, String eventAttendanceListFile, List<EventSchedule> eventSchedules){
        this.eventImage = eventImage;
        this.eventAttendanceListFile = eventAttendanceListFile;
        this.eventSchedules = eventSchedules;
    }

    public void updateAlarm(){
        this.alarmResponse = true;
    }

}
