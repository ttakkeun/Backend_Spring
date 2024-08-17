package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;


@Repository
public interface TipRepository extends JpaRepository<Tip, Long> {
    List<Tip> findByTipCategory(Category category);
}
