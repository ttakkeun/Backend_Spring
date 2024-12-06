package ttakkeun.ttakkeun_server.dto.auth.kakao;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoSignUpRequestDTO {

    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
}
