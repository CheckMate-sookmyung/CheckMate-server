package checkmate.com.checkmate.event.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.BaseTimeEntity;
import checkmate.com.checkmate.global.domain.EventType;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.user.domain.User;
import jakarta.annotation.Nullable;
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

    @Enumerated(EnumType.STRING)
    private EventTarget eventTarget;

    private String eventImage;

    private String beforeAttendanceListFile;

    private Boolean alarmRequest;

    private Boolean alarmResponse;

    private int minCompletionTimes;

    private String afterAttendanceListEachFile;

    private String afterAttendanceListTotalFile;

    @Nullable
    private String managerName;

    @Nullable
    private String managerPhoneNumber;

    @Nullable
    private String managerEmail;

    @OneToMany(mappedBy="event", cascade = CascadeType.ALL)
    private List<EventSchedule> eventSchedules = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Builder(toBuilder = true)
    public Event(String eventTitle, String eventDetail, int minCompletionTimes, boolean alarmRequest, User user, EventType eventType, EventTarget eventTarget){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.alarmRequest = alarmRequest;
        this.minCompletionTimes = minCompletionTimes;
        this.alarmResponse = false;
        this.user = user;
        this.eventType = eventType;
        this.eventTarget = eventTarget;
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

    public void updateAttendanceListFileAferEvent(String eachFileUrl, String totalFileUrl){
        this.afterAttendanceListEachFile = eachFileUrl;
        this.afterAttendanceListTotalFile = totalFileUrl;
    }

    public void updateAttendanceListFileBetweenEvent(String eachFileUrl){
        this.afterAttendanceListEachFile = eachFileUrl;
    }

    public void registerEventManager(String managerName, String managerPhoneNumber, String managerEmail){
        this.managerName = managerName;
        this.managerPhoneNumber = managerPhoneNumber;
        this.managerEmail = managerEmail;
    }

}
