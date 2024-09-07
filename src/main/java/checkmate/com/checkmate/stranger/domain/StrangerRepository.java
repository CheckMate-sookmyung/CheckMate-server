package checkmate.com.checkmate.stranger.domain;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface StrangerRepository extends JpaRepository<Stranger, Long> {
    Optional<Stranger> findByStrangerPhoneNumberAndStrangerName(String strangerPhoneNumber, String strangerName);

}
