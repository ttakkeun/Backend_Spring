package ttakkeun.ttakkeun_server.converter;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.dto.product.ProductRequestDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.service.LikeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductConverter {
    private final LikeService likeService;
    private final ProductRepository productRepository;

    //Product를 ProductDTO로 convert
    public RecommendProductDTO toDTO(Product product, Long memberId) {
        Long productId = product.getProductId();
        boolean isLike = likeService.getLikeStatus(productId, memberId);

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

    //api Response의 추천제품DTO를 제품엔티티DTO로 변경
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

    //네이버 api로 얻은 JSONObject를 ProductDTO로 변경
    public RecommendProductDTO JSONToDTO(JSONObject jsonObject, Long memberId) {
        Long productId = Long.valueOf(jsonObject.getString("productId"));
        Product product = productRepository.findById(productId).orElse(null);

        int totalLikes;
        if (product == null) {
            totalLikes = 0;
        } else {
            totalLikes = product.getTotalLikes();
        }
        boolean isLike = likeService.getLikeStatus(productId, memberId);

        return RecommendProductDTO.builder()
                .product_id(productId)
                .title(jsonObject.getString("title"))
                .image(jsonObject.getString("image"))
                .price(Integer.valueOf(jsonObject.getString("lprice")))
                .brand(jsonObject.getString("brand"))
                .link(jsonObject.getString("link"))
                .category1(jsonObject.getString("category1"))
                .category2(jsonObject.getString("category2"))
                .category3(jsonObject.getString("category3"))
                .category4(jsonObject.getString("category4"))
                .total_likes(totalLikes)
                .isLike(isLike)
                .build();
    }

    //api Request의 추천제품DTO를 제품엔티티DTO로 변경
    public Product toProduct(Long productId, ProductRequestDTO requestDTO) {
        return Product.builder()
                .productId(productId)
                .productTitle(requestDTO.getTitle())
                .productLink(requestDTO.getLink())
                .productImage(requestDTO.getImage())
                .lprice(requestDTO.getPrice())
                .brand(requestDTO.getBrand())
                .category1(requestDTO.getCategory1())
                .category2(requestDTO.getCategory2())
                .category3(requestDTO.getCategory3())
                .category4(requestDTO.getCategory4())
                .build();
    }

    //네이버 api에서 가져오는 제품을 아래 카테고리로 한정
    public boolean categoryFilter(RecommendProductDTO productDTO) {
        List<String> validCategory3 = Arrays.asList(
                "미용/목욕", "강아지 건강/관리용품", "고양이 건강/관리용품"
        );
        List<String> validCategory4 = Arrays.asList(
                "브러시/빗", "에센스/향수/밤", "샴푸/린스/비누", "이발기", "발톱/발 관리",
                "미용가위", "타월/가운", "물티슈/크리너",
                "눈/귀 관리용품", "구강청결제", "칫솔", "치약", "구강티슈", "구강관리용품"
        );

        return validCategory3.contains(productDTO.getCategory3()) &&
                validCategory4.contains(productDTO.getCategory4());
    }
}


