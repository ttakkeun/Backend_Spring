package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.MemberResponseDto;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.service.MemberService;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원정보 조회 API")
    @GetMapping("/info")
    public ApiResponse<MemberResponseDto> getMemberInfo(@AuthenticationPrincipal Member member) {

        MemberResponseDto memberResponse = new MemberResponseDto(
                member.getMemberId(),
                member.getUsername(),
                member.getEmail()
        );

        return ApiResponse.onSuccess(memberResponse);
    }

    @Operation(summary = "닉네임 수정 API")
    @PatchMapping("/username")
    public ApiResponse<String> updateUsername(
            @AuthenticationPrincipal Member member,
            @RequestParam String newUsername) {
        memberService.updateUsername(member.getMemberId(), newUsername);
        return ApiResponse.of(SuccessStatus._OK, "닉네임이 수정되었습니다.");
    }
}
