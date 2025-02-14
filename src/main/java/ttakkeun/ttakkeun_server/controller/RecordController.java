package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.record.RecordService;

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
            @RequestParam(name = "page", defaultValue = "0") int page
    ){
        List<RecordListResponseDto> records = recordService.getRecordsByCategory(member, petId, category, page, 21);
        var result = new RecordListResponse(category, records);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "일지 질문 및 답변 조회 API")
    @GetMapping("/register/{category}")
    public ApiResponse<RecordResponseDTO.LoadQuestionResultDTO> loadquestion(
            @PathVariable("category") Category category
    ) {
        List<RecordResponseDTO.QuestionDTO> questions = recordService.getQuestionsByCategory(category);
        RecordResponseDTO.LoadQuestionResultDTO result = RecordResponseDTO.LoadQuestionResultDTO.builder()
                .category(category.name())
                .questions(questions)
                .build();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "일지 생성")
    @PostMapping(value = "/create/{pet_id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<RecordResponseDTO.RegisterResultDTO> createRecord(
            @PathVariable("pet_id") Long petId,
            @ModelAttribute RecordRequestDTO.RecordRegisterDTO requestDTOs) {

        RecordResponseDTO.RegisterResultDTO responseDTO = recordService.createRecord(petId, requestDTOs);
        return ApiResponse.onSuccess(responseDTO);
    }

    @Operation(summary = "일지 상세 내용 조회 API")
    @GetMapping("/detail/{pet_id}/{record_id}")
    public ApiResponse<RecordResponseDTO.DetailResultDTO> getRecordDetails(
            @PathVariable("pet_id") Long petId,
            @PathVariable("record_id") Long recordId
    ) {
        RecordResponseDTO.DetailResultDTO recordDetails = recordService.getRecordDetails(petId, recordId);
        return ApiResponse.onSuccess(recordDetails);
    }

    @Operation(summary = "일지 삭제 API")
    @DeleteMapping("/{record_id}")
    public ApiResponse<RecordResponseDTO.DeleteResultDTO> deleteRecord(
            @PathVariable("record_id") Long record_id
    ) {
        RecordResponseDTO.DeleteResultDTO deleteResultDTO = recordService.deleteRecord(record_id);
        return ApiResponse.onSuccess(deleteResultDTO);
    }

    @Operation(summary = "일지 기록 검색 API")
    @GetMapping("/search/{pet_id}/{category}")
    public ApiResponse<RecordListResponse> getRecordListAtDate(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "pet_id") Long petId,
            @PathVariable(name = "category") Category category,
            @RequestParam(name = "date") String date
    ){
        List<RecordListResponseDto> records = recordService.getRecordsAtDate(member, petId, category, date);
        RecordListResponse result = new RecordListResponse(category, records);
        return ApiResponse.onSuccess(result);
    }


}
