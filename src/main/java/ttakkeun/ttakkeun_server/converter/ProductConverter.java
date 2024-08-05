package ttakkeun.ttakkeun_server.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.ProductDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.service.LikeService;

@Component
@RequiredArgsConstructor
public class ProductConverter {
    private final LikeService likeService;

    //Product를 ProductDTO로 convert
    public ProductDTO toDTO(Product product) {
        Long productId = product.getProductId();
        //임시 멤버값
        boolean isLike = likeService.getLikeStatus(productId);

        return ProductDTO.builder()
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

    public Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .productId(productDTO.getProduct_id())
                .productTitle(productDTO.getTitle())
                .productImage(productDTO.getImage())
                .lprice(productDTO.getPrice())
                .brand(productDTO.getBrand())
                .productLink(productDTO.getLink())
                .category1(productDTO.getCategory1())
                .category2(productDTO.getCategory2())
                .category3(productDTO.getCategory3())
                .category4(productDTO.getCategory4())
                .build();
    }
}
