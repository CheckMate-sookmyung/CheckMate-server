package checkmate.com.checkmate.eventAttendance.domain.repository;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.student.studentNumber = :studentNumber")
    EventAttendance findByEventScheduleIdAndStudentNumber(
            @Param("eventScheduleId") Long eventScheduleId,
            @Param("studentNumber") int studentNumber
    );
    @Query("SELECT ea FROM EventAttendance ea WHERE ea.id = :eventAttendanceId")
    EventAttendance findByEventAttendanceId(@Param("eventAttendanceId") Long eventAttendanceId);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id= :eventScheduleId")
    List<EventAttendance> findEventAttendancesById(@Param("eventScheduleId") Long eventScheduleId);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.stranger.strangerPhoneNumber IS NOT NULL AND SUBSTRING(ea.stranger.strangerPhoneNumber, -4) = :phoneNumberSuffix")
    List<EventAttendance> findAllByEventScheduleIdAndPhoneNumberSuffix(@Param("eventScheduleId") Long eventScheduleId, @Param("phoneNumberSuffix") String phoneNumberSuffix);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id = :eventScheduleId")
    List<EventAttendance> findTotalAttendeeByEventScheduleId(@Param("eventScheduleId") Long eventScheduleId);

    @Query("SELECT ea FROM EventAttendance ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.attendance = true")
    List<EventAttendance> findAverageAttendeeByEventScheduleId(@Param("eventScheduleId") Long eventScheduleId);

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.eventSchedule.eventScheduleId IN :eventScheduleIds AND ea.student = :student AND ea.attendance = true")
    int countAttendanceByEventScheduleIdsAndStudent(@Param("eventScheduleIds") List<Long> eventScheduleIds,
                                                    @Param("student") Student student);

}
