package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.diagnose.deleteDiagnoseResponseDTO;
import ttakkeun.ttakkeun_server.dto.tip.PostTipReportRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.PostTipReportResponseDTO;
import ttakkeun.ttakkeun_server.dto.tip.ScrapTipResponseDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipCreateRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.ScrapTipService;
import ttakkeun.ttakkeun_server.service.TipService;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tips")
public class TipController {

    private final TipService tipService;
    private final ScrapTipService scrapTipService;

    @Operation(summary = "팁 생성 API")
    @PostMapping("/add")
    public ApiResponse<TipResponseDTO> createTip(
            @AuthenticationPrincipal Member member,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") Category category) {

        TipCreateRequestDTO request = new TipCreateRequestDTO(title, content, category);
        TipResponseDTO result = tipService.createTip(request, member.getMemberId());
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

    @Operation(summary = "부위 별 팁 조회 API")
    @GetMapping
    public ApiResponse<List<TipResponseDTO>> getTips(
            @AuthenticationPrincipal Member member,
            @RequestParam("category") Category category,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        List<TipResponseDTO> result = tipService.getTipsByCategory(category, page, 21, member);
        return ApiResponse.onSuccess(result);
    }


    @Operation(summary = "전체 팁 조회 API")
    @GetMapping("/all")
    public ApiResponse<List<TipResponseDTO>> getAllTips(
            @AuthenticationPrincipal Member member,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        List<TipResponseDTO> result = tipService.getAllTips(page, 21, member);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "Best 팁 조회 API")
    @GetMapping("/best")
    public ApiResponse<List<TipResponseDTO>> getBestTips(
            @AuthenticationPrincipal Member member
    ) {
        List<TipResponseDTO> result = tipService.getBestTips(member);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "팁 삭제 API")
    @DeleteMapping("/{tip_id}")
    public ApiResponse<Void> deleteTips(
            @PathVariable("tip_id") Long tipId
    ) {
        tipService.deleteTip(tipId);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "팁 스크랩/취소 토글 기능")
    @PatchMapping("/scrap/{tip_id}")
    public ApiResponse<ScrapTipResponseDTO> toggleScrapTips(
            @AuthenticationPrincipal Member member,
            @PathVariable("tip_id") Long tipId
    ) {
        scrapTipService.toggleScrapTip(tipId, member);
        ScrapTipResponseDTO result = scrapTipService.getScrapStatus(tipId, member);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "내가 작성한 팁 조회 API")
    @GetMapping("/myTips")
    public ApiResponse<List<TipResponseDTO>> getMyTips(
            @AuthenticationPrincipal Member member,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        List<TipResponseDTO> result = tipService.getMyTips(member, page);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "내가 스크랩한 팁 조회 API")
    @GetMapping("/myScraps")
    public ApiResponse<List<TipResponseDTO>> getScrapTips(
            @AuthenticationPrincipal Member member,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        List<TipResponseDTO> result = tipService.getScrapTips(member, page);

        return ApiResponse.onSuccess(result);
    }

    // 팁 신고하기 API
    @Operation(summary = "팁 신고하기")
    @PostMapping(value = "/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostTipReportResponseDTO>> postTipReport(
        @AuthenticationPrincipal Member member,
        @RequestPart("postTipReportRequestDTO") PostTipReportRequestDTO postTipReportRequestDTO,
        @RequestPart(required = false) List<MultipartFile> multipartFile
        ) {
        boolean reportResult = false;
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }

            reportResult = tipService.postTipReport(member, postTipReportRequestDTO, multipartFile);

            if (!reportResult) {
                throw new IllegalArgumentException("신고 접수에 실패했습니다. 다시 시도해주세요");
            }

            ApiResponse<PostTipReportResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new PostTipReportResponseDTO("게시글 신고에 성공하였습니다."));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ApiResponse<PostTipReportResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.TIP_REPORT_FAILURE, new PostTipReportResponseDTO("게시글 신고에 실패했습니다. 다시 시도해주세요"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<PostTipReportResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, new PostTipReportResponseDTO("게시글 신고에 실패했습니다. 다시 시도해주세요"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
