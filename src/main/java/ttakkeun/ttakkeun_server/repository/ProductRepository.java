package ttakkeun.ttakkeun_server.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //진단결과 id를 이용해 진단제품 불러오기
    @Query("SELECT p FROM Product p JOIN p.resultProductList rpl WHERE rpl.result.resultId = :resultId")
    List<Product> findByResultId(Long resultId);

    //product 데이터베이스에서 좋아요 순으로 불러오기, 페이지네이션 이용
    @Query("SELECT p FROM Product p ORDER BY p.totalLikes DESC, p.productId DESC")
    Page<Product> sortedByLikesWithPaging(Pageable pageable);

    //product 데이터베이스에서 부위별로 태그 상품을 가져옴
    @Query("SELECT p FROM Product p WHERE p.tag = :tag ORDER BY p.totalLikes DESC, p.productId DESC")
    Page<Product> findByTag(@Param("tag") Category tag, Pageable pageable);
}
