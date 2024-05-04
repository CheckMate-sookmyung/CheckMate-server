package checkmate.com.checkmate.eventattendancelist.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class EventAttendanceList {
    @Id @GeneratedValue
    private Long EventAttendanceListId;

    private String name;
    private String studentNumber;
    private String major;
    private String phoneNumber;
    private String email;
    private Integer attendanceRate;
    private String sign;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;
}
