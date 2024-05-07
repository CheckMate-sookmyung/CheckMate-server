package checkmate.com.checkmate.event.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
