package ttakkeun.ttakkeun_server.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;

    //제품id로 제품 불러오기
    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    //진단결과 id를 이용해 진단제품 불러오기
    public List<Product> findByResultId(Long resultId) {
        return em.createQuery("SELECT p FROM Product p WHERE p.result.resultId = :resultId", Product.class)
                .setParameter("resultId", resultId)
                .setMaxResults(5)
                .getResultList();
    }
}
