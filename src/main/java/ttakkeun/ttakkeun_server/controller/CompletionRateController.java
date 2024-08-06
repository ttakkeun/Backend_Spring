package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.CompletionRateDto;
import ttakkeun.ttakkeun_server.service.CompletionRateService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class CompletionRateController {

    private final CompletionRateService completionRateService;

    @Operation(summary = "일정 완수율 조회 API")
    @GetMapping("/completion-rate")
    public ApiResponse<CompletionRateDto> getCompletionRate(
            // @RequestHeader("Authorization") String accessToken,
            @RequestParam Long petId) {
        CompletionRateDto result = completionRateService.getCompletionRate(petId);
        return ApiResponse.onSuccess(result);
    }
}
