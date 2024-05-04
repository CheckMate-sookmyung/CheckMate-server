package checkmate.com.checkmate.eventattendancelist.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
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
    private int attendanceRate;
    private String sign;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    @Builder(toBuilder = true)
    public EventAttendanceList(final String name, final String studentNumber, final String major, final String phoneNumber, final String email, final int attendanceRate, final String sign){
        this.name = name;
        this.studentNumber = studentNumber;
        this.major = major;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.attendanceRate = 0;
        this.sign = sign;
    }

    /* 알고리즘 수정 필요
       += 100 / 전체 일정 개수
       전체 일정 개수 조회하는 메소드 필요
    */
    public void updateOneAttendanceRate(){
        this.attendanceRate += 100;
    }
}
