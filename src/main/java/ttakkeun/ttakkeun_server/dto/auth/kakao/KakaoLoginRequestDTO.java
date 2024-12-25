package ttakkeun.ttakkeun_server.dto.auth.kakao;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoLoginRequestDTO {

    @NotEmpty
    private String accessToken;

    private String email;
    private String name;
}
