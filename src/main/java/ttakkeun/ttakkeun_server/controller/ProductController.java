package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.LikeResponseDTO;
import ttakkeun.ttakkeun_server.dto.product.ProductApiResponseDTO;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.dto.product.ProductRequestDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.LikeService;
import ttakkeun.ttakkeun_server.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LikeService likeService;
    private final MemberRepository memberRepository;

    //ai 추천 제품
    @Operation(summary = "가장 최근 진단 기준 최대 5개의 추천 제품 조회 API")
    @GetMapping("/ai/{pet_id}")
    public ApiResponse<List<RecommendProductDTO>> getAiProducts(
            @PathVariable Long pet_id,
            @AuthenticationPrincipal Member member
    ) {
        Long memberId = member.getMemberId();
        List<RecommendProductDTO> products = productService.getResultProducts(pet_id, memberId);

        return ApiResponse.onSuccess(products);
    }

    //랭킹별 추천제품
    @Operation(summary = "랭킹별 추천 제품(전체) 조회 API")
    @GetMapping("/rank/{page}")
    public ApiResponse<List<RecommendProductDTO>> getRankedProducts(
            @PathVariable int page,
            @AuthenticationPrincipal Member member
    ) {
        Long memberId = member.getMemberId();
        List<RecommendProductDTO> products = productService.getRankedProducts(page, memberId);

        return ApiResponse.onSuccess(products);
    }

    //부위 별 랭킹 제품
    @Operation(summary = "랭킹별 추천 제품(태그) 조회 API")
    @GetMapping("/tag/{tag}/{page}")
    public ApiResponse<List<RecommendProductDTO>> getTagRankingProducts(
            @PathVariable Category tag, @PathVariable int page,
            @AuthenticationPrincipal Member member
    ) {
        Long memberId = member.getMemberId();
        List<RecommendProductDTO> products = productService.getTagRankingProducts(tag, page, memberId);

        return ApiResponse.onSuccess(products);
    }

    @Operation(summary = "검색 조회(따끈 DB) API")
    @GetMapping("/search_db/{keyword}/{page}")
    public ApiResponse<List<RecommendProductDTO>> getSearchProductsFromDB(
            @PathVariable String keyword, @PathVariable int page,
            @AuthenticationPrincipal Member member
    ) {
        Long memberId = member.getMemberId();
        List<RecommendProductDTO> products = productService.getProductByKeywordFromDB(keyword, page, memberId);

        return ApiResponse.onSuccess(products);
    }

    @Operation(summary = "검색 조회(네이버 쇼핑) API")
    @GetMapping("/search_naver/{keyword}")
    public ApiResponse<List<RecommendProductDTO>> getSearchProductsFromNaver(
            @PathVariable String keyword,
            @AuthenticationPrincipal Member member
    ) {
        Long memberId = member.getMemberId();
        List<RecommendProductDTO> products = productService.getProductByKeywordFromNaver(keyword, memberId);

        return ApiResponse.onSuccess(products);
    }

    @Operation(summary = "좋아요/취소 토글 API")
    @PatchMapping("/like/{product_id}")
    public ApiResponse<LikeResponseDTO.Result> toggleLikeProduct(
            @PathVariable Long product_id,
            @AuthenticationPrincipal Member member,
            @RequestBody ProductRequestDTO requestBody
    ) {
        Long memberId = member.getMemberId();
        productService.addNewProduct(product_id, requestBody);
        likeService.toggleLikeProduct(product_id, memberId);

        LikeResponseDTO.Result result = likeService.getLikeInfo(product_id, memberId);

        return ApiResponse.onSuccess(result);
    }
}
