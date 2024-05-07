package checkmate.com.checkmate.event.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.id = :eventId")
    Event findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId")
    List<Event> findByUserId(@Param("userId") Long userId);

}
