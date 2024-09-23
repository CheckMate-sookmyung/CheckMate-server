package checkmate.com.checkmate.eventAttendance.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.global.BaseTimeEntity;
import checkmate.com.checkmate.stranger.domain.Stranger;
import checkmate.com.checkmate.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendance extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long EventAttendanceId;

    @Column
    private boolean attendance;

    @Column
    private String sign;

    @Column
    private Integer accessTime;

    @Column
    private LocalDateTime attendTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="eventSchedule_id")
    private EventSchedule eventSchedule;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="stranger_id")
    private Stranger stranger;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="student_id")
    private Student student;

    @Builder
    public EventAttendance(EventSchedule eventSchedule, Stranger stranger, Student student, boolean attendance) {
        this.eventSchedule = eventSchedule;
        this.stranger = stranger;
        this.student = student;
        this.attendance = attendance;
    }

    public void updateAttendanceByAttendanceCheck(String imageUrl){
        this.sign = imageUrl;
        this.attendance = true;
        this.attendTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public void updateAttendanceByManager(boolean attendance){
        this.attendance = attendance;
    }

    public void updateAttendanceAboutOnlineEvent(boolean attendance, int accessTime){
        this.attendance = attendance;
        this.accessTime = accessTime;

    }

}
