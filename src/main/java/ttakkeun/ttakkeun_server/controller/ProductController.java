//package ttakkeun.ttakkeun_server.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ttakkeun.ttakkeun_server.dto.ProductApiResponseDTO;
//import ttakkeun.ttakkeun_server.dto.ProductDTO;
//import ttakkeun.ttakkeun_server.service.ProductService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/product")
//@RequiredArgsConstructor
//public class ProductController {
//    private final ProductService productService;
//
//    //ai 추천 제품
//    @GetMapping("/ai")
//    public ResponseEntity<ProductApiResponseDTO> getAiProducts() {
//        try {
//            List<ProductDTO> products = productService.getResultProducts();
//            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
//                    .isSuccess(true)
//                    .code(200)
//                    .message("성공")
//                    .result(products)
//                    .build();
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            ProductApiResponseDTO response = ProductApiResponseDTO.builder()
//                    .isSuccess(false)
//                    .code(500)
//                    .message("에러: " + e.getMessage())
//                    .build();
//
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//}
//
