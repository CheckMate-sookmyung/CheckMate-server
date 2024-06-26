package checkmate.com.checkmate.event.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.BaseTimeEntity;
import checkmate.com.checkmate.global.domain.EventType;
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
public class Event extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long eventId;

    private String eventTitle;

    private String eventDetail;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String eventImage;

    private String beforeAttendanceListFile;

    private Boolean alarmRequest;

    private Boolean alarmResponse;

    private String afterAttendanceListFile;

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
        this.beforeAttendanceListFile = eventAttendanceListFile;
        this.alarmRequest = alarmResponse;
    }

    public void postFileAndAttendanceList(String eventImage, String eventAttendanceListFile, List<EventSchedule> eventSchedules){
        this.eventImage = eventImage;
        this.beforeAttendanceListFile = eventAttendanceListFile;
        this.eventSchedules = eventSchedules;
    }

    public void updateAlarm(){
        this.alarmResponse = true;
    }

    public void updateAttendanceListFileAferEvent(String fileUrl){
        this.afterAttendanceListFile = fileUrl;
    }

}
