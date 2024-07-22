package checkmate.com.checkmate.eventattendanceList.domain.repository;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventAttendanceListRepository extends JpaRepository<EventAttendanceList, Long> {

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.studentNumber = :studentNumber")
    EventAttendanceList findByEventIdAndStudentNumber(@Param("eventScheduleId") Long eventScheduleId, @Param("studentNumber") int studentNumber);

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.id = :eventAttendanceListId")
    EventAttendanceList findByEventAttendanceListId(@Param("eventAttendanceListId") Long eventAttendanceListId);

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.eventSchedule.id= :eventScheduleId")
    List<EventAttendanceList> findEventAttendanceListById(@Param("eventScheduleId") Long eventScheduleId);

    @Query(value = "SELECT * FROM event_attendance_list WHERE event_schedule_id = :eventScheduleId AND SUBSTRING(phone_number, -4) = :phoneNumberSuffix", nativeQuery = true)
    EventAttendanceList findByEventIdAndPhoneNumberSuffix(@Param("eventScheduleId") Long eventScheduleId, @Param("phoneNumberSuffix") String phoneNumberSuffix);
}
