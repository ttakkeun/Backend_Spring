package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.entity.enums.Category;



@Repository
public interface TipRepository extends JpaRepository<Tip, Long> {
    Page<Tip> findByTipCategory(Category category, Pageable pageable);
}
