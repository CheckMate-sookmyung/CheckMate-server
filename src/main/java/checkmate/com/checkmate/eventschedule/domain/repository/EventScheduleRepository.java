package checkmate.com.checkmate.eventschedule.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    @Modifying
    @Query("DELETE FROM EventSchedule es WHERE es.event.id = :eventId")
    void deleteEventSchedulesByEventId(Long eventId);

    void deleteByEventEventId(Long eventId);

}

