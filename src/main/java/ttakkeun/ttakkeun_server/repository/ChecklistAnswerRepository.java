package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.ChecklistAnswer;

public interface ChecklistAnswerRepository extends JpaRepository<ChecklistAnswer, Long> {
}
