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

    //product 데이터베이스에서 좋아요 순으로 불러오기, 페이지네이션 이용
    public List<Product> sortedByLikesWithPaging(int page, int pageSize) {
        int cursor = page * pageSize;

        return em.createQuery("SELECT p FROM Product p " +
                "order by p.totalLikes desc, p.productId desc", Product.class)
                .setFirstResult(cursor)
                .setMaxResults(pageSize)
                .getResultList();
    }

    //product 데이터베이스에서 부위별로 태그 상품을 가져옴
    public List<Product> findByTag(String tag, int page, int pageSize) {
        int cursor = page * pageSize;

        return em.createQuery("SELECT p FROM Product p WHERE p.tag = :tag " +
                        "ORDER BY p.totalLikes DESC, p.productId DESC", Product.class)
                .setParameter("tag", tag)
                .setFirstResult(cursor)  // 조회 시작 지점
                .setMaxResults(pageSize)  // 페이지 크기
                .getResultList();
    }
}
