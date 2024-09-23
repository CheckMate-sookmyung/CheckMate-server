package checkmate.com.checkmate.stranger.domain.repository;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.stranger.domain.Stranger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface StrangerRepository extends JpaRepository<Stranger, Long> {
    Optional<Stranger> findByStrangerPhoneNumberAndStrangerNameAndMemberMemberId(String strangerPhoneNumber, String strangerName, Long memberId);

}
