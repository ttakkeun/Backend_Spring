package ttakkeun.ttakkeun_server.dto.auth.kakao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserDTO {
    private Long id; // 카카오 유저 ID
    private KakaoAccount kakao_account;

    public static class KakaoAccount {
        private String email;
        private String profileNickname;
    }
}
