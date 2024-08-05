package ttakkeun.ttakkeun_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.dto.LikeResponseDTO;
import ttakkeun.ttakkeun_server.dto.ProductApiResponseDTO;
import ttakkeun.ttakkeun_server.dto.ProductDTO;
import ttakkeun.ttakkeun_server.entity.LikeProduct;
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
    @GetMapping("/ai")
    public ResponseEntity<ProductApiResponseDTO> getAiProducts() {
        try {
            List<ProductDTO> products = productService.getResultProducts();
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
    @GetMapping("/rank/{page}")
    public ResponseEntity<ProductApiResponseDTO> getRankedProducts(@PathVariable int page) {
        try {
            List<ProductDTO> products = productService.getRankedProducts(page);
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
    @GetMapping("/tag/{tag}/{page}")
    public ResponseEntity<ProductApiResponseDTO> getTagRankingProducts(@PathVariable String tag, @PathVariable int page) {
        try {
            List<ProductDTO> products = productService.getTagRankingProducts(tag, page);
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

    @PatchMapping("/like/{product_id}")
    public ResponseEntity<LikeResponseDTO> addLikeProduct(@RequestBody Long product_id) {
        try {
            LikeResponseDTO.Result result = likeService.addLikeProduct(product_id);

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
