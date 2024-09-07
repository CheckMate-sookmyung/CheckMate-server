package checkmate.com.checkmate.eventAttendance.domain.repository;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.studentNumber = :studentNumber")
    EventAttendance findByEventIdAndStudentNumber(@Param("eventScheduleId") Long eventScheduleId, @Param("studentNumber") int studentNumber);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.id = :eventAttendanceId")
    EventAttendance findByEventAttendanceId(@Param("eventAttendanceId") Long eventAttendanceId);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id= :eventScheduleId")
    List<EventAttendance> findEventAttendanceById(@Param("eventScheduleId") Long eventScheduleId);

    @Query(value = "SELECT * FROM event_attendance WHERE event_schedule_id = :eventScheduleId AND SUBSTRING(phone_number, -4) = :phoneNumberSuffix", nativeQuery = true)
    List<EventAttendance> findAllByEventScheduleIdAndPhoneNumberSuffix(@Param("eventScheduleId") Long eventScheduleId, @Param("phoneNumberSuffix") String phoneNumberSuffix);

}
