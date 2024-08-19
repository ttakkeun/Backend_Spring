package ttakkeun.ttakkeun_server.controller;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.diagnose.*;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.service.DiagnoseService.DiagnoseChatGPTService;
import ttakkeun.ttakkeun_server.service.DiagnoseService.DiagnoseService;
//import ttakkeun.ttakkeun_server.dto.UpdateProductsDTO;

import java.util.NoSuchElementException;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.PET_ID_NOT_AVAILABLE;
import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.RECORD_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/diagnose")
public class DiagnoseTestController {
    @Autowired
    private DiagnoseService diagnoseService;

    @Autowired
    private DiagnoseChatGPTService diagnoseChatGPTService;

    // 진단 버튼 클릭시 사용자의 포인트를 조회하는 API
    @Operation(summary = "사용자 포인트 조회 테스트 API")
    @GetMapping("/point")
    public ResponseEntity<ApiResponse<GetMyPointResponseDTO>> getPointsByMember() {
        try {
            // memberId가 1인 유저로 테스트함
            Integer point = diagnoseService.getPointsByMember(1L);
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new GetMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_HAS_NO_POINT, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // 진단 결과 목록을 조회하는 API
    // 페이징 처리, 한 페이지당 10개 고정
    // size가 고정이므로 Path Variable로 처리하였음
    @Operation(summary = "진단 결과 목록 조회 API 테스트")
    @GetMapping("/{pet_id}/{category}/{page}")
    public ResponseEntity<ApiResponse<GetMyDiagnoseListResponseDTO>> getDiagnoseListByPet(@PathVariable(name = "pet_id") Long petId,
                                                                                          @PathVariable(name = "category") Category category,
                                                                                          @PathVariable(name = "page") int page) {
        try {
            // memberId가 1인 유저로 테스트함
            GetMyDiagnoseListResponseDTO getMyDiagnoseListResponseDTO = diagnoseService.getDiagnoseListByPet(1L, petId, category, page);
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.of(SuccessStatus._OK, getMyDiagnoseListResponseDTO);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.PET_ID_NOT_AVAILABLE, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @Operation(summary = "AI 진단하기 API")
    @PostMapping("/loading")
    public ResponseEntity<ApiResponse<PostDiagnoseResponseDTO>> postDiagnoseByRecord(@RequestBody PostDiagnoseRequestDTO records) {
        try {
            // memberId가 1인 유저로 테스트함
            PostDiagnoseResponseDTO postDiagnoseResponseDTO = diagnoseChatGPTService.postDiagnoseByRecord(1L, records);
            ApiResponse<PostDiagnoseResponseDTO> response = ApiResponse.of(SuccessStatus._OK, postDiagnoseResponseDTO);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<PostDiagnoseResponseDTO> response = ApiResponse.ofFailure(RECORD_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<PostDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (NullPointerException e) {
            ApiResponse<PostDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            ApiResponse<PostDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // 진단시 사용자 포인트 차감 API
    @Operation(summary = "사용자 포인트 차감 테스트 API")
    @PatchMapping("/loading")
    public ResponseEntity<ApiResponse<UpdateMyPointResponseDTO>> updatePointsByMember() {
        try {
            // memberId가 1인 유저로 테스트함
            Integer point = diagnoseService.updatePointsByMember(1L);
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new UpdateMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_HAS_NO_POINT, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}