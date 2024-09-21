package checkmate.com.checkmate.mail.domain.repository;

import checkmate.com.checkmate.mail.domain.Mail;
import checkmate.com.checkmate.mail.domain.MailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
    @Query("SELECT m FROM Mail m WHERE m.event.id = :eventId AND m.mailType = :mailType")
    Mail findByEventIdAndMailType(@Param("eventId") Long eventId, @Param("mailType")MailType mailType);

    @Query("SELECT m FROM Mail m WHERE m.event.id = :eventId")
    List<Mail> findByEventId(@Param("eventId") Long eventId);
}
