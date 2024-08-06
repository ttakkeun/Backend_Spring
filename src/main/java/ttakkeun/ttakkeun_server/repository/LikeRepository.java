package ttakkeun.ttakkeun_server.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Product;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final EntityManager em;

    //멤버와 연관된 제품들 반환(특정 사용자의 좋아요 목록)
    public List<Product> findProductsByMemberId(Long memberId) {
        return em.createQuery("SELECT lp.product FROM LikeProduct lp WHERE lp.member.memberId = :memberId", Product.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    //제품과 연관된 멤버들 반환(특정 제품에 좋아요한 사용자 목록)
    public List<Member> findMembersByProductId(Long productId) {
        return em.createQuery("SELECT lp.member FROM LikeProduct lp WHERE lp.product.productId = :productId", Member.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
