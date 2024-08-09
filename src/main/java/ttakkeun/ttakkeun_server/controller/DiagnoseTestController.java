package ttakkeun.ttakkeun_server.controller;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.diagnose.GetMyDiagnoseResponseDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.GetMyPointResponseDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.UpdateMyPointResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.DiagnoseService;
//import ttakkeun.ttakkeun_server.dto.UpdateProductsDTO;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/diagnose")
public class DiagnoseTestController {
    @Autowired
    private DiagnoseService diagnoseService;

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
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
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
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}