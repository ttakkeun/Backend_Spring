package ttakkeun.ttakkeun_server.service.auth;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleAuthClient;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleLoginRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleRevokeRequest;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.LoginType;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.JwtService;
import ttakkeun.ttakkeun_server.service.MemberService;
import ttakkeun.ttakkeun_server.service.PetService;
import ttakkeun.ttakkeun_server.utils.AppleClientSecretGenerator;
import ttakkeun.ttakkeun_server.utils.AppleOAuthProvider;
import ttakkeun.ttakkeun_server.utils.ApplePublicKeyGenerator;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    @Autowired
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final AppleOAuthProvider appleOAuthProvider;
    private final JwtService jwtService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Value("${spring.social-login.provider.apple.client-id}")
    private String clientId;

    //accessToken, refreshToken 발급
    @Transactional
    public LoginResponseDto createToken(Member member) {
        String newAccessToken = jwtService.generateAccessToken(member.getMemberId());
        String newRefreshToken = jwtService.generateRefreshToken(member.getMemberId());

        System.out.println("newAccessToken : " + newAccessToken);
        System.out.println("newRefreshToken : " + newRefreshToken);

        // DB에 refreshToken 저장
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println("member nickname : " + member.getUsername());

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    // refreshToken으로 accessToken 발급하기
    @Transactional
    public LoginResponseDto regenerateAccessToken(String refreshToken) {

        if (!jwtService.validateTokenBoolean(refreshToken))  // refresh token 유효성 검사
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        Long memberId = jwtService.getMemberIdFromJwtToken(refreshToken);
        log.info("memberId : " + memberId);

        Optional<Member> getMember = memberRepository.findById(memberId);
        if (getMember.isEmpty())
            throw new ExceptionHandler(MEMBER_NOT_FOUND);

        Member member = getMember.get();
        if (!refreshToken.equals(member.getRefreshToken()))
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        String newRefreshToken = jwtService.generateRefreshToken(memberId);
        String newAccessToken = jwtService.generateAccessToken(memberId);

        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println("member nickname : " + member.getUsername());

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    //애플 로그인
    @Transactional
    public LoginResponseDto appleLogin(AppleLoginRequestDto appleLoginRequestDto) {
        log.info("Current time is {}", LocalDateTime.now());
        // 1. 애플 공개 키 가져오기
        Map<String, String> headers = jwtService.parseHeader(appleLoginRequestDto.getIdentityToken());
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        // 2. JWT 검증 및 클레임 추출
        Claims claims = jwtService.getTokenClaims(appleLoginRequestDto.getIdentityToken(), publicKey);

        // 3. 클레임에서 subject 추출
        String sub = claims.getSubject();

        // 4. 유저가 등록되어 있는지 확인
        Member member = memberRepository.findByAppleSub(sub)
                .orElse(null);

        if (member == null) {
            // 등록된 유저가 아닌 경우 회원가입 로직
            throw new ExceptionHandler(MEMBER_NOT_REGISTERED);
        }

        // 5. 토큰 생성 및 반환
        return createToken(member);
    }

    // 애플 회원가입
    @Transactional
    public LoginResponseDto appleSignUp(AppleSignUpRequestDto appleSignUpRequestDto) {

        // 1. 애플 공개 키 가져오기
        Map<String, String> headers = jwtService.parseHeader(appleSignUpRequestDto.getIdentityToken());
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        // 2. JWT 검증 및 클레임 추출
        Claims claims = jwtService.getTokenClaims(appleSignUpRequestDto.getIdentityToken(), publicKey);

        // 3. 클레임에서 subject 추출
        String email = claims.get("email", String.class);
        String sub = claims.getSubject();

        // 4. 유저가 등록되어 있는지 확인
        Member member = memberRepository.findByAppleSub(sub).orElse(null);

        if (member == null) {
            // 등록된 유저가 아닌 경우 회원가입 로직
            member = memberRepository.save(
                    Member.builder()
                            .email(email)
                            .username(appleSignUpRequestDto.getUserName()) // appleLoginRequestDto에서 닉네임 가져오기
                            .appleSub(sub)
                            .loginType(LoginType.APPLE)
                            .refreshToken("") // 초기 빈 값 설정
                            .refreshTokenExpiresAt(LocalDateTime.now()) // 초기 시간 설정
                            .build()
            );
        }

        // 5. 토큰 생성 및 반환
        return createToken(member);
    }

    @Transactional
    public void appleDelete(Member member, String code) {

        try {
            String clientSecret = appleClientSecretGenerator.createClientSecret();
            String refreshToken = appleOAuthProvider.getAppleRefreshToken(code, clientSecret);

            AppleRevokeRequest appleRevokeRequest = AppleRevokeRequest.builder()
                    .client_id(clientId)
                    .refresh_token(refreshToken)
                    .client_secret(clientSecret)
                    .token_type("REFRESH_TOKEN")
                    .build();
            appleAuthClient.revoke(appleRevokeRequest);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Apple Revoke Error");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("애플 탈퇴 성공");
        log.info("member id :: " + member.getMemberId());

        memberService.deleteMember(member);
    }
}