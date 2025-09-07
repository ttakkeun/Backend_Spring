package ttakkeun.ttakkeun_server.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleAuthClient;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleTokenRequest;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleTokenResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOAuthProvider {

    private final AppleAuthClient appleAuthClient;
    @Value("${spring.social-login.provider.apple.client-id}")
    private String clientId;

    public String getAppleRefreshToken(String code, String clientSecret) {
        AppleTokenRequest appleTokenRequest = AppleTokenRequest.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .grant_type("authorization_code")
                .build();
        return appleAuthClient.findAppleToken(appleTokenRequest).refreshToken();
    }

    public String getAppleIdToken(String code, String clientSecret) {
        AppleTokenRequest appleTokenRequest = AppleTokenRequest.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .grant_type("authorization_code")
                .build();

        return appleAuthClient.findAppleToken(appleTokenRequest).idToken();
    }
}
