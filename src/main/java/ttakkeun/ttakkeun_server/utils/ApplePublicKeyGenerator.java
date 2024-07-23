package ttakkeun.ttakkeun_server.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.auth.apple.ApplePublicKeyResponse;

import javax.naming.AuthenticationException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j

public class ApplePublicKeyGenerator {
    public PublicKey generatePublicKey(Map<String, String> tokenHeaders,
                                       ApplePublicKeyResponse applePublicKeys)
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApplePublicKeyResponse.ApplePublicKey publicKey = applePublicKeys.getMatchedKeyBy(tokenHeaders.get("kid"),
                tokenHeaders.get("alg"));

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKeyResponse.ApplePublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.getE());

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes),
                new BigInteger(1, eBytes));


        KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getKty());
        return keyFactory.generatePublic(publicKeySpec);
    }

}
