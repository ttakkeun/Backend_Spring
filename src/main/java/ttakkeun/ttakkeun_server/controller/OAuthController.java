package ttakkeun.ttakkeun_server.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleLoginRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.service.auth.OAuthService;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.APPLE_ID_TOKEN_EMPTY;
import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.TOKEN_EMPTY;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDto> regenerateAccessToken(HttpServletRequest request) {
        //String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshToken");

        //System.out.println("Access Token: " + accessToken.substring(7));
        System.out.println("Refresh Token: " + refreshToken.substring(7));

        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
            LoginResponseDto result = oAuthService.regenerateAccessToken(refreshToken.substring(7));
            return ApiResponse.onSuccess(result);
        } else
            throw new ExceptionHandler(TOKEN_EMPTY);
    }

    @PostMapping("/apple/login")
    public ApiResponse<LoginResponseDto> appleLogin(@RequestBody @Validated AppleLoginRequestDto appleReqDto) throws Exception {
        if (appleReqDto.getIdentityToken() == null)
            throw new ExceptionHandler(APPLE_ID_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.appleLogin(appleReqDto));
    }

    @PostMapping("/apple/signup")
    public ApiResponse<LoginResponseDto> appleSignUp(@RequestBody @Validated AppleSignUpRequestDto appleSignUpReqDto) throws Exception{
        if (appleSignUpReqDto.getIdentityToken() == null)
            throw new ExceptionHandler(APPLE_ID_TOKEN_EMPTY);
        return ApiResponse.onSuccess(oAuthService.appleSignUp(appleSignUpReqDto));
    }
}
