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
import ttakkeun.ttakkeun_server.dto.diagnose.GetMyDiagnoseResponseDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.GetMyPointResponseDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.UpdateMyPointResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.service.DiagnoseService;
//import ttakkeun.ttakkeun_server.dto.UpdateProductsDTO;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnose")
public class DiagnoseController {
    @Autowired
    private DiagnoseService diagnoseService;

    // 진단 버튼 클릭시 사용자의 포인트를 조회하는 API
    @Operation(summary = "사용자 포인트 조회 API")
    @GetMapping("/point")
    public ResponseEntity<ApiResponse<GetMyPointResponseDTO>> getPointsByMember(@AuthenticationPrincipal Member member) {
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }
            Long memberId = member.getMemberId(); // 인증된 사용자의 memberId를 가져옴
            Integer point = diagnoseService.getPointsByMember(memberId);
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
    @Operation(summary = "사용자 포인트 차감 API")
    @PatchMapping("/loading")
    public ResponseEntity<ApiResponse<UpdateMyPointResponseDTO>> updatePointsByMember(@AuthenticationPrincipal Member member) {
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }
            Long memberId = member.getMemberId(); // 인증된 사용자의 memberId를 가져옴
            Integer point = diagnoseService.updatePointsByMember(memberId);
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

    // 진단서 상세 내용 조회 API
    @Operation(summary = "진단서 상세 내용 조회 API")
    @GetMapping("/{diagnose_id}")
    public ResponseEntity<ApiResponse<GetMyDiagnoseResponseDTO>> getDiagnose(@PathVariable("diagnose_id") Long resultId) {
        try {
            GetMyDiagnoseResponseDTO diagnose = diagnoseService.getDiagnose(resultId);
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.of(SuccessStatus._OK, diagnose);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.DIAGNOSE_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}