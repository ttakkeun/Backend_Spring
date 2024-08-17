package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.tip.*;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.TipService;

import java.util.List;

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
    @PatchMapping(value = "/{tip_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadTipImage(
            @AuthenticationPrincipal Member member,
            @PathVariable("tip_id") Long tipId,
            @RequestPart("image") MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 없습니다.");
        }

        String imageUrl = tipService.uploadTipImage(tipId, member.getMemberId(), multipartFile);
        return ApiResponse.onSuccess(imageUrl);
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
}