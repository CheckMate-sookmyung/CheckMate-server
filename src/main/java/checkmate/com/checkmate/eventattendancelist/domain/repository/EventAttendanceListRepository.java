package checkmate.com.checkmate.eventattendancelist.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventattendancelist.domain.EventAttendanceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventAttendanceListRepository extends JpaRepository<EventAttendanceList, Long> {

    @Query("SELECT ea FROM EventAttendanceList ea WHERE ea.eventSchedule.id = :eventScheduleId AND ea.studentNumber = :studentNumber")
    EventAttendanceList findByEventIdAndStudentNumber(@Param("eventScheduleId") Long eventScheduleId, @Param("studentNumber") int studentNumber);
}
