package ttakkeun.ttakkeun_server.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.service.LikeService;

@Component
@RequiredArgsConstructor
public class ProductConverter {
    private final LikeService likeService;

    //Product를 ProductDTO로 convert
    public RecommendProductDTO toDTO(Product product) {
        Long productId = product.getProductId();
        //임시 멤버값
        boolean isLike = likeService.getLikeStatus(productId);

        return RecommendProductDTO.builder()
                .product_id(product.getProductId())
                .title(product.getProductTitle())
                .image(product.getProductImage())
                .price(product.getLprice())
                .brand(product.getBrand())
                .link(product.getProductLink())
                .category1(product.getCategory1())
                .category2(product.getCategory2())
                .category3(product.getCategory3())
                .category4(product.getCategory4())
                .total_likes(product.getTotalLikes())
                .isLike(isLike)
                .build();
    }

    public Product toEntity(RecommendProductDTO recommendProductDTO) {
        return Product.builder()
                .productId(recommendProductDTO.getProduct_id())
                .productTitle(recommendProductDTO.getTitle())
                .productImage(recommendProductDTO.getImage())
                .lprice(recommendProductDTO.getPrice())
                .brand(recommendProductDTO.getBrand())
                .productLink(recommendProductDTO.getLink())
                .category1(recommendProductDTO.getCategory1())
                .category2(recommendProductDTO.getCategory2())
                .category3(recommendProductDTO.getCategory3())
                .category4(recommendProductDTO.getCategory4())
                .build();
    }
}
