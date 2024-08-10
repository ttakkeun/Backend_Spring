package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.RecordService;

import java.util.List;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "일지 목록 조회 API")
    @GetMapping("/{pet_id}/{category}")
    public ApiResponse<RecordListResponse> getRecordList(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "pet_id") Long petId, @PathVariable(name = "category") Category category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "21") int size
    ){
        System.out.println("일지 목록 조회 API Controller");
        List<RecordListResponseDto> records = recordService.getRecordsByCategory(member, petId, category, page, size);
        RecordListResponse result = new RecordListResponse(category, records);
        return ApiResponse.onSuccess(result);
    }

    // 테스트용 메서드
    @Operation(summary = "일지 목록 조회 API 테스트")
    @GetMapping("/test/{pet_id}/{category}")
    public ApiResponse<RecordListResponse> getRecordListForTest(
            @PathVariable(name = "pet_id") Long petId, @PathVariable(name = "category") Category category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "21") int size
    ){
        // 임의의 Member 객체 생성
        Member testMember = new Member();
        testMember.setMemberId(1L); // 임의의 memberId 설정
        System.out.println("Member ID: " + testMember.getMemberId());

        List<RecordListResponseDto> records = recordService.getRecordsByCategory(testMember, petId, category, page, size);
        RecordListResponse result = new RecordListResponse(category, records);
        return ApiResponse.onSuccess(result);
    }
}
