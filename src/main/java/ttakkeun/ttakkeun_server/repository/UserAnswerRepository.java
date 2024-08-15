package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.UserAnswer;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
