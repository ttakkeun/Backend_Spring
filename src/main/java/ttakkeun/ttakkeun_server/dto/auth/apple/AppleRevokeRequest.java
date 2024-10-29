package ttakkeun.ttakkeun_server.dto.auth.apple;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AppleRevokeRequest {
    private String client_id;
    private String client_secret;
    private String refresh_token;
    private String token_type;
}
