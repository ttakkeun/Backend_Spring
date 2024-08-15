package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.ChecklistAnswer;
import ttakkeun.ttakkeun_server.entity.UserAnswer;

import java.util.Optional;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    Optional<UserAnswer> findByRecord_recordIdAndQuestion_questionId(Long recordId, Long questionId);
}
