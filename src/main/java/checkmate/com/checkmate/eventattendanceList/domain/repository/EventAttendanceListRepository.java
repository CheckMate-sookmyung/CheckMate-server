package checkmate.com.checkmate.eventattendanceList.domain.repository;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventAttendanceListRepository extends JpaRepository<EventAttendanceList, Long> {

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.studentNumber = :studentNumber")
    EventAttendanceList findByEventIdAndStudentNumber(@Param("eventScheduleId") Long eventScheduleId, @Param("studentNumber") int studentNumber);

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.id = :eventAttendanceListId")
    EventAttendanceList findByEventAttendanceListId(@Param("eventAttendanceListId") Long eventAttendanceListId);
}