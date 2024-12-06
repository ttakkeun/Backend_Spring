package ttakkeun.ttakkeun_server.dto.auth.apple;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppleSignUpRequestDto {

    @NotEmpty
    private String identityToken;
    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
}
