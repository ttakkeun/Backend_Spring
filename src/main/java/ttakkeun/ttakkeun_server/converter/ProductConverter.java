//package ttakkeun.ttakkeun_server.converter;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ttakkeun.ttakkeun_server.dto.ProductDTO;
//import ttakkeun.ttakkeun_server.entity.Product;
//import ttakkeun.ttakkeun_server.service.LikeService;
//
//@Component
//@RequiredArgsConstructor
//public class ProductConverter {
//    private final LikeService likeService;
//
//    //Product를 ProductDTO로 convert
//    public ProductDTO toDTO(Product product) {
//        Long productId = product.getProductId();
//        //임시 멤버값
//        Integer totalLikes = likeService.getTotalLikes(productId);
//        boolean isLike = likeService.getLikeStatus(productId);
//
//        return ProductDTO.builder()
//                .product_id(product.getProductId())
//                .title(product.getProductTitle())
//                .image(product.getProductImage())
//                .price(product.getLprice())
//                .brand(product.getBrand())
//                .link(product.getProductLink())
//                .category1(product.getCategory1())
//                .category2(product.getCategory2())
//                .category3(product.getCategory3())
//                .category4(product.getCategory4())
//                .total_likes(totalLikes)
//                .isLike(isLike)
//                .build();
//    }
//}
