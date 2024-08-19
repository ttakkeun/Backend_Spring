package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.tip.TipCreateRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.TipService;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tips")
public class TipController {

    private final TipService tipService;

    @Operation(summary = "팁 생성 API")
    @PostMapping("/add")
    public ApiResponse<TipResponseDTO> createTip(
            @AuthenticationPrincipal Member member,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") Category category) {

        TipCreateRequestDTO request = new TipCreateRequestDTO(title, content, category);
        TipResponseDTO result = tipService.createTip(request, member.getMemberId(), null);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "팁 이미지 업로드 API")
    @PatchMapping(value = "/{tip_id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<String>> uploadTipImages(
            @AuthenticationPrincipal Member member,
            @PathVariable("tip_id") Long tipId,
            @RequestParam("images") List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("이미지가 없습니다.");
        }

        List<TipImage> tipImages = tipService.uploadTipImages(tipId, member.getMemberId(), images);
        List<String> imageUrls = tipImages.stream()
                .map(TipImage::getTipImageUrl)
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(imageUrls);
    }

    @Operation(summary = "팁 조회 API")
    @GetMapping
    public ApiResponse<List<TipResponseDTO>> getTips(
            @RequestParam("category") Category category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<TipResponseDTO> result = tipService.getTipsByCategory(category, page, size);
        return ApiResponse.onSuccess(result);
    }


    @Operation(summary = "전체 팁 조회 API")
    @GetMapping("/all")
    public ApiResponse<List<TipResponseDTO>> getAllTips(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<TipResponseDTO> result = tipService.getAllTips(page, size);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "Best 팁 조회 API")
    @GetMapping("/best")
    public ApiResponse<List<TipResponseDTO>> getBestTips(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<TipResponseDTO> result = tipService.getBestTips(page, size);
        return ApiResponse.onSuccess(result);


}
}
