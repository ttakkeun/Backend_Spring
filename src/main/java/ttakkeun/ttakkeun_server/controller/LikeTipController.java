package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.dto.tip.LikeTipResponseDTO;
import ttakkeun.ttakkeun_server.service.LikeTipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tips")
public class LikeTipController {

    private final LikeTipService likeTipService;

    @Operation(summary = "팁 좋아요/취소 토글 API")
    @PatchMapping("/like/{tip_id}")
    public ResponseEntity<LikeTipResponseDTO> toggleTipLike(
            @PathVariable("tip_id") Long tipId,
            @RequestHeader("Authorization") Long memberId) {

        likeTipService.toggleLikeTip(tipId, memberId);

        int totalLikes = likeTipService.getTotalTipLikes(tipId);
        boolean isLike = likeTipService.getTipLikeStatus(tipId, memberId);

        LikeTipResponseDTO response = LikeTipResponseDTO.builder()
                .totalLikes(totalLikes)
                .isLike(isLike)
                .build();

        return ResponseEntity.ok(response);
    }
}
