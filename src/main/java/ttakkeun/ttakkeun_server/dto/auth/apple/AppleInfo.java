package ttakkeun.ttakkeun_server.dto.auth.apple;

import lombok.Builder;

public class AppleInfo {
    private String email;
    private String sub;

    @Builder
    public AppleInfo(String email, String sub) {
        this.email = email;
        this.sub = sub;
    }
}
