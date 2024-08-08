package ttakkeun.ttakkeun_server.repository.custom;

import ttakkeun.ttakkeun_server.entity.Product;

import java.util.List;

public interface CustomLikeRepository {
    List<Product> findProductsByMemberId(Long memberId);
}
