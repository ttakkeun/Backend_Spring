package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Product;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 진단 결과 ID를 이용해 진단 제품 불러오기
    @Query("SELECT p FROM Product p WHERE p.result.resultId = :resultId")
    List<Product> findByResultId(Long resultId);

    List<Product> findByResultResultId(Long resultId);
}