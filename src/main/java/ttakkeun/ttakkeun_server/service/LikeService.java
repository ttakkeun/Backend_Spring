package ttakkeun.ttakkeun_server.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.Like;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.LikeProductConverter;
import ttakkeun.ttakkeun_server.dto.LikeResponseDTO;
import ttakkeun.ttakkeun_server.entity.LikeProduct;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.repository.LikeRepository;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;

    //사용자의 좋아요 판단
    public Boolean getLikeStatus(Long productId, Long memberId) {
        Product targetProduct = productRepository.findById(productId).orElse(null);
        return likeRepository.findProductsByMemberId(memberId).contains(targetProduct);
    }

    //좋아요 버튼 토글 기능
    public void toggleLikeProduct(Long productId, Long memberId) {
        Optional<LikeProduct> likeProductOpt = likeRepository.findByProductIdAndMemberId(productId, memberId);

        if(likeProductOpt.isPresent()) { //이미 존재할 경우 좋아요 테이블에서 삭제
            likeRepository.delete(likeProductOpt.get());
        } else { //존재하지 않을 경우 테이블에 추가
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

            LikeProduct likeProduct = new LikeProduct(null, product, member);
            likeRepository.save(likeProduct);
        }
    }

    //좋아요 수, 상태 반환
    public LikeResponseDTO.Result getLikeInfo(Long productId, Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        em.refresh(product);

        int totalLike = product.getTotalLikes();
        Boolean isLike = getLikeStatus(productId, memberId);

        return LikeProductConverter.toDTO(totalLike, isLike);
    }

    public void deleteAllByMember(Member member) {
        likeRepository.deleteByMember(member);
    }
}
