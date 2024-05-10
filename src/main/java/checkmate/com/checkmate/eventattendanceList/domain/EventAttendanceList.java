package checkmate.com.checkmate.eventattendanceList.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendanceList {
    @Id @GeneratedValue
    private Long EventAttendanceListId;

    private String name;
    private int studentNumber;
    private String major;
    private String phoneNumber;
    private String email;
    private boolean attendance;
    private String sign;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="eventSchedule_id")
    private EventSchedule eventSchedule;

    @Builder(toBuilder = true)
    public EventAttendanceList(final String name, final int studentNumber, final String major, final String phoneNumber, final String email, EventSchedule eventSchedule){
        this.name = name;
        this.studentNumber = studentNumber;
        this.major = major;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.eventSchedule = eventSchedule;
        this.attendance = false;
    }

    public void updateAttendance(String imageUrl){
        this.sign = imageUrl;
        this.attendance = true;
    }
}