package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
            @RequestParam("category") Category category,
            @RequestParam("images") List<MultipartFile> imageFiles) {

        TipCreateRequestDTO request = new TipCreateRequestDTO(member.getMemberId(), title, content, category);
        TipResponseDTO result = tipService.createTip(request, member.getMemberId(), imageFiles);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "팁 조회 API")
    @GetMapping
    public ApiResponse<List<TipResponseDTO>> getTips(
            @RequestParam("category") Category category) {
        List<TipResponseDTO> result = tipService.getTipsByCategory(category);
        return ApiResponse.onSuccess(result);
    }
}
