package ttakkeun.ttakkeun_server.controller;

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

    @GetMapping("/completion-rate")
    public ApiResponse<CompletionRateDto> getCompletionRate(
            // @RequestHeader("Authorization") String accessToken,
            @RequestParam Long petId) {
        CompletionRateDto result = completionRateService.getCompletionRate(petId);
        return ApiResponse.onSuccess(result);
    }
}
