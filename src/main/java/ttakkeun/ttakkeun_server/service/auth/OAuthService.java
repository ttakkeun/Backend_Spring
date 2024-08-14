package ttakkeun.ttakkeun_server.service.auth;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleAuthClient;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleLoginRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.LoginType;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.JwtService;
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

    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

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
//        if(jwtService.validateTokenBoolean(accessToken))  // access token 유효성 검사
//            throw new ExceptionHandler(ACCESS_TOKEN_UNAUTHORIZED);

        if(!jwtService.validateTokenBoolean(refreshToken))  // refresh token 유효성 검사
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        Long memberId = jwtService.getMemberIdFromJwtToken(refreshToken);
        log.info("memberId : " + memberId);

        Optional<Member> getMember = memberRepository.findById(memberId);
        if(getMember.isEmpty())
            throw new ExceptionHandler(MEMBER_NOT_FOUND);

        Member member = getMember.get();
        if(!refreshToken.equals(member.getRefreshToken()))
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        String newRefreshToken = jwtService.generateRefreshToken(memberId);
        String newAccessToken = jwtService.generateAccessToken(memberId);

        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println("member nickname : " + member.getUsername());

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public LoginResponseDto appleLogin(AppleLoginRequestDto appleLoginRequestDto) throws Exception {
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

    // 애플 로그인 (회원가입 해야하는 경우)
    @Transactional
    public LoginResponseDto appleSignUp(AppleSignUpRequestDto appleSignUpRequestDto) throws Exception{

        // 1. 애플 공개 키 가져오기
        Map<String, String> headers = jwtService.parseHeader(appleSignUpRequestDto.getIdentityToken());
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        // 2. JWT 검증 및 클레임 추출
        Claims claims = jwtService.getTokenClaims(appleSignUpRequestDto.getIdentityToken(), publicKey);

        // 3. 클레임에서 subject 추출
        String sub = claims.getSubject();

        // 4. 유저가 등록되어 있는지 확인
        Member member = memberRepository.findByAppleSub(sub).orElse(null);

        if (member == null) {
            // 등록된 유저가 아닌 경우 회원가입 로직
            member = memberRepository.save(
                    Member.builder()
                            //.email(claims.get("email", String.class)) // 애플 JWT에 이메일 클레임이 포함된 경우 사용
                            //.nickname(appleSignUpRequestDto.getName()) // appleLoginRequestDto에서 닉네임 가져오기
                            //.provider(MemberProvider.APPLE) // 필요에 따라 설정
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
