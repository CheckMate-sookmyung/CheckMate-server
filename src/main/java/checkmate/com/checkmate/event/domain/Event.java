package checkmate.com.checkmate.event.domain;

import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.BaseTimeEntity;
import checkmate.com.checkmate.global.domain.EventStatus;
import checkmate.com.checkmate.global.domain.EventType;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.member.domain.Member;
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

    @Column(nullable = false)
    private String eventTitle;

    @Column
    private String eventDetail;

    @Column
    private String eventImage;

    @Column
    private EventStatus eventStatus;

    private String beforeAttendanceListFile;

    @Column
    private String afterAttendanceListExcelFile;

    @Column
    private String afterAttendanceListPDFFile;

    @Column
    private double eventAttendanceRatio;


    @Column(nullable = false)
    private int completionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventTarget eventTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column
    private String managerName;

    @Column
    private String managerEmail;

    @Column
    private String managerPhoneNumber;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @Builder(toBuilder = true)
    public Event(String eventTitle, String eventDetail, int completionTime, Member member, EventType eventType, EventTarget eventTarget, String managerEmail){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.completionTime = completionTime;
        this.member = member;
        this.eventType = eventType;
        this.eventTarget = eventTarget;
        this.managerEmail = managerEmail;
        this.eventAttendanceRatio = 0.0;
    }

    public void registerFileAndAttendanceList(String eventImage, String eventAttendanceListFile){
        this.eventImage = eventImage;
        this.beforeAttendanceListFile = eventAttendanceListFile;
    }

    public void updateEvent(String eventTitle, String eventDetail, String eventImage){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.eventImage = eventImage;
    }

    public void updateAttendanceListFile(String afterAttendanceListPDFFile, String afterAttendanceListExcelFile){
        this.afterAttendanceListPDFFile = afterAttendanceListPDFFile;
        this.afterAttendanceListExcelFile = afterAttendanceListExcelFile;
    }

    public void registerEventManager(String managerName, String managerPhoneNumber, String managerEmail){
        this.managerName = managerName;
        this.managerPhoneNumber = managerPhoneNumber;
        this.managerEmail = managerEmail;
    }


    public void updateEventAttendanceRatio(double eventAttendanceRatio){
        this.eventAttendanceRatio = eventAttendanceRatio;
    }

    public void changeEventStatus(EventStatus eventStatus){
        this.eventStatus = eventStatus;
    }

}
