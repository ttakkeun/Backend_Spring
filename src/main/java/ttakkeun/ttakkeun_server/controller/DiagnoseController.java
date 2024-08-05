package ttakkeun.ttakkeun_server.controller;

import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.GetMyPointResponseDTO;
import ttakkeun.ttakkeun_server.dto.UpdateMyPointResponseDTO;
import ttakkeun.ttakkeun_server.service.DiagnoseService;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnose")
public class DiagnoseController {

    @Autowired
    private DiagnoseService diagnoseService;

    // memberId 임의로 입력받아서 조회하는 방식으로 구현함
    // 추후 로그인 정보로 멤버 아이디 받아와서 조회하는 방식으로 수정 예정
    @GetMapping("/point")
    public ResponseEntity<ApiResponse<GetMyPointResponseDTO>> getPointsByMember(@RequestParam("member-id") Long memberId) {
        try {
            Integer point = diagnoseService.getPointsByMember(memberId);
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new GetMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/loading")
    public ResponseEntity<ApiResponse<UpdateMyPointResponseDTO>> updatePointsByMember(@RequestParam("member-id") Long memberId) {
        try {
            Integer point = diagnoseService.updatePointsByMember(memberId);
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new UpdateMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
