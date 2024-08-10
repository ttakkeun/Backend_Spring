package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.ChecklistQuestion;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;

@Repository
public interface ChecklistQuestionRepository extends JpaRepository<ChecklistQuestion, Long> {
    List<ChecklistQuestion> findByQuestionCategory(Category category);
}