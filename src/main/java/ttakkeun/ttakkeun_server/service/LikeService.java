package ttakkeun.ttakkeun_server.service;

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

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    //사용자의 좋아요 판단
    public Boolean getLikeStatus(Long productId) {

        //임시 멤버값
        Long memberId = 526L;
        Product targetProduct = productRepository.findById(productId).orElse(null);
        return likeRepository.findProductsByMemberId(memberId).contains(targetProduct);
    }

    //좋아요를 누른 멤버와 그 제품을 저장
    public LikeResponseDTO.Result addLikeProduct(Long productId) {
        //임시 멤버값 사용
        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));;
        Member targetMember = memberRepository.findById(526L)
                .orElseThrow(() -> new RuntimeException("Member not found with id: 526L"));;

        //LikeProduct테이블에 저장
        LikeProduct likeProduct = new LikeProduct(null, targetProduct, targetMember);
        likeRepository.save(likeProduct);

        //좋아요 수, 좋아요 상태 값
        int totalLikes = productRepository.findById(productId).orElse(null).getTotalLikes();
        Boolean isLike = getLikeStatus(productId);

        return LikeProductConverter.toDTO(totalLikes, isLike);
    }
}
