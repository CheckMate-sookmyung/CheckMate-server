package checkmate.com.checkmate.event.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.member.id = :memberId AND e.id = :eventId")
    Event findByMemberIdAndEventId(@Param("memberId") Long memberId, @Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e WHERE e.member.id = :memberId")
    List<Event> findByMemberId(@Param("memberId") Long memberId);

}
