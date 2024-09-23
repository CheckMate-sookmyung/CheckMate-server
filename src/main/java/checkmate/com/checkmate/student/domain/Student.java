package checkmate.com.checkmate.student.domain;

import checkmate.com.checkmate.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Student {
    @Id
    @GeneratedValue
    private Long studentId;

    @Column
    private String studentName;

    @Column(nullable = false)
    private int studentNumber;

    @Column
    private String studentMajor;

    @Column
    private String studentPhoneNumber;

    @Column
    private String studentEmail;

    @Column
    private float attendanceRate;

    @Column
    private int attendanceTime;

    @Column
    private int applicationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Student(final String studentName,
                   final int studentNumber,
                   final String studentMajor,
                   final String studentPhoneNumber,
                   final String studentEmail,
                   final Member member
                   ) {
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.studentMajor = studentMajor;
        this.studentPhoneNumber = studentPhoneNumber;
        this.studentEmail = studentEmail;
        this.attendanceRate = 0.0F;
        this.attendanceTime = 0;
        this.applicationTime = 1;
        this.member = member;
    }

    public void updateApplication() {
        this.applicationTime += 1;
    }

    public void updateAttendance() {
        this.attendanceRate = (float) (this.attendanceTime + 1) / this.applicationTime * 100;
        this.attendanceTime += 1;
    }
}
