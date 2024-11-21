package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.LikeProduct;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.repository.custom.CustomLikeRepository;

import java.util.Optional;


@Repository
public interface LikeRepository extends JpaRepository<LikeProduct, Long>, CustomLikeRepository {

    @Query("SELECT lp FROM LikeProduct lp WHERE lp.product.productId = :productId AND lp.member.memberId = :memberId")
    Optional<LikeProduct> findByProductIdAndMemberId(@Param("productId") Long productId, @Param("memberId") Long memberId);

    void deleteByMember(Member member);
}
