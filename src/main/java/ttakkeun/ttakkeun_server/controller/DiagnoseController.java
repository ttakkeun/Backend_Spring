package ttakkeun.ttakkeun_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.GetMyPointResponseDTO;
import ttakkeun.ttakkeun_server.service.DiagnoseService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnose")
public class DiagnoseController {

    @Autowired
    private DiagnoseService diagnoseService;

    // memberId 임의로 입력받아서 조회하는 방식으로 구현함
    // 추후 액세스 토큰으로 멤버 아이디 받아와서 조회하는 방식으로 수정 예정
    @GetMapping("/point")
    public ApiResponse<GetMyPointResponseDTO> getPointsByMember(@RequestParam("member-id") Long memberId) {
        try {
            Integer point = diagnoseService.getPointsByMember(memberId);
            return ApiResponse.of(SuccessStatus._OK, new GetMyPointResponseDTO(point));
        } catch (Exception e) {
            return ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
        }
    }
}
