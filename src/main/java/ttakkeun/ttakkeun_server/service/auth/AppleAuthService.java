package ttakkeun.ttakkeun_server.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.TokenDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleAuthClient;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleInfo;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleUser;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.JwtService;
import ttakkeun.ttakkeun_server.utils.ApplePublicKeyGenerator;

import javax.naming.AuthenticationException;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginResponseDto appleLogin(String identityToken) throws Exception {
        // 1. 애플 공개 키 가져오기
        Map<String, String> headers = jwtService.parseHeader(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        // 2. JWT 검증 및 클레임 추출
        Claims claims = jwtService.getTokenClaims(identityToken, publicKey);

        // 3. 클레임에서 subject 추출
        String sub = claims.getSubject();

        // 4. 유저가 등록되어 있는지 확인
        Member member = memberRepository.findByAppleSub(sub)
                .orElseThrow(() -> new Exception("등록된 유저가 아닙니다."));

        // 5. 토큰 생성 및 반환
        return createToken(member);
    }

    @Transactional
    public LoginResponseDto createToken(Member member) {
        String newAccessToken = jwtService.encodeJwtToken(new TokenDto(member.getMemberId()));
        String newRefreshToken = jwtService.encodeJwtRefreshToken(member.getMemberId());

        System.out.println("newAccessToken : " + newAccessToken);
        System.out.println("newRefreshToken : " + newRefreshToken);

        // DB에 refreshToken 저장
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println("member nickname : " + member.getNickname());

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

//    public String getAppleAccountId(String identityToken)
//            throws JsonProcessingException, AuthenticationException, NoSuchAlgorithmException,
//            InvalidKeySpecException {
//        Map<String, String> headers = jwtService.parseIdentityToken(identityToken);
//        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers,
//                appleAuthClient.getAppleAuthPublicKey());
//
//        return jwtService.getTokenClaims(identityToken, publicKey).getSubject();
//    }
}
