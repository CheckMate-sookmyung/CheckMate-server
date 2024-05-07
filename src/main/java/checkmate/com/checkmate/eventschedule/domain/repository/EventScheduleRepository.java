package checkmate.com.checkmate.eventschedule.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    @Query("SELECT es FROM EventSchedule es WHERE es.event.id = :eventId")
    List<EventSchedule> findEventScheduleByEventId(@Param("eventId") Long eventID);
}

