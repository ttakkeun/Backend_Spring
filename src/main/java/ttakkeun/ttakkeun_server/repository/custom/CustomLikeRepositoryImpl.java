package ttakkeun.ttakkeun_server.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.repository.LikeRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomLikeRepositoryImpl implements CustomLikeRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> findProductsByMemberId(Long memberId) {
        return em.createQuery("SELECT lp.product FROM LikeProduct lp WHERE lp.member.memberId = :memberId", Product.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
