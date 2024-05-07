package checkmate.com.checkmate.user.domain.repository;

import checkmate.com.checkmate.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long userId);
}
