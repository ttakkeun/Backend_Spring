//package ttakkeun.ttakkeun_server.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.relational.core.sql.Like;
//import org.springframework.stereotype.Service;
//import ttakkeun.ttakkeun_server.entity.Product;
//import ttakkeun.ttakkeun_server.repository.LikeRepository;
//import ttakkeun.ttakkeun_server.repository.ProductRepository;
//
//import java.util.List;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class LikeService {
//    private final LikeRepository likeRepository;
//    private final ProductRepository productRepository;
//
//    //제품의 좋아요 수 반환
//    public int getTotalLikes(Long productId) {
//        return likeRepository.findMembersByProductId(productId).size();
//    }
//
//    //사용자의 좋아요 판단
//    public Boolean getLikeStatus(Long productId) {
//
//        //임시 멤버값
//        Long memberId = 526L;
//        Product targetProduct = productRepository.findById(productId);
//        return likeRepository.findProductsByMemberId(memberId).contains(targetProduct);
//    }
//}
