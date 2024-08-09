package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.dto.LikeResponseDTO;
import ttakkeun.ttakkeun_server.dto.product.ProductApiResponseDTO;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.LikeService;
import ttakkeun.ttakkeun_server.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LikeService likeService;

    //ai 추천 제품
    @Operation(summary = "가장 최근 진단 기준 최대 5개의 추천 제품 조회 API")
    @GetMapping("/ai")
    public ResponseEntity<ProductApiResponseDTO> getAiProducts() {
        try {
            List<RecommendProductDTO> products = productService.getResultProducts();
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    //랭킹별 추천제품
    @Operation(summary = "랭킹별 추천 제품(전체) 조회 API")
    @GetMapping("/rank/{page}")
    public ResponseEntity<ProductApiResponseDTO> getRankedProducts(@PathVariable int page) {
        try {
            List<RecommendProductDTO> products = productService.getRankedProducts(page);
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    //부위 별 랭킹 제품
    @Operation(summary = "랭킹별 추천 제품(태그) 조회 API")
    @GetMapping("/tag/{tag}/{page}")
    public ResponseEntity<ProductApiResponseDTO> getTagRankingProducts(
            @PathVariable Category tag, @PathVariable int page
    ) {
        try {
            List<RecommendProductDTO> products = productService.getTagRankingProducts(tag, page);
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "검색 조회(따끈 DB) API")
    @GetMapping("/search_db/{keyword}/{page}")
    public ResponseEntity<ProductApiResponseDTO> getSearchProductsFromDB(
            @PathVariable String keyword, @PathVariable int page
    ) {
        try {
            List<RecommendProductDTO> products = productService.getProductByKeywordFromDB(keyword, page);
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "검색 조회(네이버 쇼핑) API")
    @GetMapping("/search_naver/{keyword}")
    public ResponseEntity<ProductApiResponseDTO> getSearchProductsFromNaver(
            @PathVariable String keyword
    ) {
        try {
            List<RecommendProductDTO> products = productService.getProductByKeywordFromNaver(keyword);
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(products)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "좋아요/취소 토글 API")
    @PatchMapping("/like/{product_id}")
    public ResponseEntity<LikeResponseDTO> toggleLikeProduct(@PathVariable Long product_id) {
        try {
            likeService.toggleLikeProduct(product_id);
            LikeResponseDTO.Result result = likeService.getLikeInfo(product_id);


            LikeResponseDTO response = LikeResponseDTO.builder()
                    .isSuccess(true)
                    .code(200)
                    .message("성공")
                    .result(result).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LikeResponseDTO response = LikeResponseDTO.builder()
                    .isSuccess(false)
                    .code(500)
                    .message("에러: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }
}
