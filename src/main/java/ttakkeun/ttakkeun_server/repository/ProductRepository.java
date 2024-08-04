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

    //product데이터 베이스에서 좋아요 순으로 불러오기, 페이지네이션 이용
    public List<Product> sortedByLikesWithPaging(int page, int pageSize) {
        int cursor = page * pageSize;

        return em.createQuery("SELECT p FROM Product p " +
                "order by p.totalLikes desc, p.productId desc " +
                "LIMIT :page OFFSET :cursor", Product.class)
                .setParameter("page", page)
                .setParameter("cursor", cursor)
                .getResultList();
    }
}
