package ttakkeun.ttakkeun_server.dto.auth.apple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ApplePublicKeyResponse {
    private List<ApplePublicKey> keys;

    //받은 public key 중 kid alg 같은거 찾기
    // Identity Token 헤더에 있는것과비교
    public ApplePublicKey getMatchedKeyBy(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findAny()
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.INVALID_APPLE_ID_TOKEN_INFO));
    }
}
