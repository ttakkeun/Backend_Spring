package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponse;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.RecordService;

import java.util.List;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus.IMAGE_SUCCESS;

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


    @Operation(summary = "일지 질문 조회 API")
    @GetMapping("/register/{category}")
    public ApiResponse<RecordResponseDTO.LoadQuestionResultDTO> loadquestion(
            @PathVariable("category") Category category
    ) {
        List<RecordResponseDTO.QuestionDTO> questions = recordService.getQuestionsByCategory(category);
        RecordResponseDTO.LoadQuestionResultDTO result = new RecordResponseDTO.LoadQuestionResultDTO(category.name(), questions);
        return ApiResponse.onSuccess(result);
    }


    @Operation(summary = "일지 답변 저장 API (일지 생성)")
    @PostMapping("/register/{pet_id}")
    public ApiResponse<RecordResponseDTO.RegisterResultDTO> registerRecord(
            @PathVariable("pet_id") Long petId,
            @RequestBody RecordRequestDTO.RecordRegisterDTO requestDTO
    ) {
        RecordResponseDTO.RegisterResultDTO responseDTO = recordService.registerRecord(petId, requestDTO);
        return ApiResponse.onSuccess(responseDTO);
    }

  
    @Operation(summary = "일지 사진 저장 API")
    @PostMapping(value = "/register/{recordId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse uploadImages(
            @PathVariable(name = "recordId") Long recordId,
            @RequestPart("question_id") Long questionId,
            @RequestPart("images") List<MultipartFile> images
//            @RequestPart List<RecordRequestDTO.RecordImageDTO> recordImageDTO
    )
    {
        recordService.uploadImages(recordId,questionId, images);
        return ApiResponse.onSuccess(IMAGE_SUCCESS);
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
            @PathVariable(name = "pet_id") Long petId,
            @PathVariable(name = "category") Category category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "21") int size,
            @RequestParam(name = "date") String date
    ){
        // 임의의 Member 객체 생성
        Member testMember = new Member();
        testMember.setMemberId(1L); // 임의의 memberId 설정
        System.out.println("Member ID: " + testMember.getMemberId());

        List<RecordListResponseDto> records = recordService.getRecordsAtDate(testMember, petId, category, page, size, date);
        RecordListResponse result = new RecordListResponse(category, records);
        return ApiResponse.onSuccess(result);
    }
}
