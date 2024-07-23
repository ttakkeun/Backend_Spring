package ttakkeun.ttakkeun_server.dto.auth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth")
public interface AppleAuthClient {
    @GetMapping
    ApplePublicKeyResponse getAppleAuthPublicKey();
}
