package ttakkeun.ttakkeun_server.controller;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.OAuthHandler;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleLoginRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoLoginRequestDTO;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoSignUpRequestDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.service.MemberService;
import ttakkeun.ttakkeun_server.service.auth.OAuthService;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class OAuthController {

    private final OAuthService oAuthService;

    @Operation(summary = "토큰 재발급 API")
    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDto> regenerateAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");

        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
            LoginResponseDto result = oAuthService.regenerateAccessToken(refreshToken.substring(7));
            return ApiResponse.onSuccess(result);
        } else
            throw new OAuthHandler(TOKEN_EMPTY);
    }

    @Operation(summary = "애플 로그인 API")
    @PostMapping("/apple/login")
    public ApiResponse<LoginResponseDto> appleLogin(@RequestBody @Validated AppleLoginRequestDto appleReqDto) {
        if (appleReqDto.getIdentityToken() == null)
            throw new OAuthHandler(APPLE_ID_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.appleLogin(appleReqDto));
    }

    @Operation(summary = "애플 회원가입 API")
    @PostMapping("/apple/signup")
    public ApiResponse<LoginResponseDto> appleSignUp(@RequestBody @Validated AppleSignUpRequestDto appleSignUpReqDto) {
        if (appleSignUpReqDto.getIdentityToken() == null)
            throw new OAuthHandler(APPLE_ID_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.appleSignUp(appleSignUpReqDto));
    }

    @Operation(summary = "애플 탈퇴 API")
    @DeleteMapping("/apple/delete")
    public ApiResponse<String> appleWithdraw(@AuthenticationPrincipal Member member,
                                @Nullable @RequestHeader("authorization-code") final String code){
        oAuthService.appleDelete(member, code);

        return ApiResponse.onSuccess("apple delete success");
    }

    @Operation(summary = "카카오 로그인 API")
    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponseDto> kakaoLogin(@RequestBody @Validated KakaoLoginRequestDTO kakaoReqDto) {
        if (kakaoReqDto.getAccessToken() == null)
            throw new OAuthHandler(KAKAO_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.kakaoLogin(kakaoReqDto));
    }

    @Operation(summary = "카카오 회원가입 API")
    @PostMapping("/kakao/signup")
    public ApiResponse<LoginResponseDto> kakaoSignUp(@RequestBody @Validated KakaoSignUpRequestDTO kakaoSignUpReqDto) {
        if (kakaoSignUpReqDto.getAccessToken() == null)
            throw new OAuthHandler(KAKAO_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.kakaoSignUp(kakaoSignUpReqDto));
    }

    @Operation(summary = "카카오 탈퇴 API(아직 구현 못함)")
    @DeleteMapping("/kakao/delete")
    public ApiResponse<String> kakaoWithdraw(@AuthenticationPrincipal Member member,
                                        @Nullable @RequestHeader("kakao-target-id") final String code){
        oAuthService.kakaoDelete(member, code);
        return ApiResponse.onSuccess("kakao delete success");
    }

    @Operation(summary = "회원 로그아웃 API")
    @PostMapping("/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal Member member) {
        String token = member.getRefreshToken();

        if (StringUtils.hasText(token)) {
            oAuthService.logout(token);
            return ApiResponse.onSuccess("LOGOUT SUCCESS");
        } else
            throw new ExceptionHandler(TOKEN_EMPTY);
    }
}
