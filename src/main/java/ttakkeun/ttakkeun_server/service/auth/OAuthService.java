package ttakkeun.ttakkeun_server.service.auth;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.MemberHandler;
import ttakkeun.ttakkeun_server.client.KakaoUnlinkClient;
import ttakkeun.ttakkeun_server.converter.MemberConverter;
import ttakkeun.ttakkeun_server.dto.auth.LoginResponseDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleAuthClient;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleLoginRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleRevokeRequest;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoLoginRequestDTO;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoSignUpRequestDTO;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoUserDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.LoginType;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.service.MemberService;
import ttakkeun.ttakkeun_server.utils.AppleClientSecretGenerator;
import ttakkeun.ttakkeun_server.utils.AppleOAuthProvider;
import ttakkeun.ttakkeun_server.utils.ApplePublicKeyGenerator;

import java.security.PublicKey;
import java.util.Map;
import java.util.Optional;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final AppleOAuthProvider appleOAuthProvider;
    private final JwtService jwtService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;
    private final KakaoUnlinkClient kakaoUnlinkClient;

    @Value("${spring.social-login.provider.apple.client-id}")
    private String clientId;

    @Value("${spring.social-login.provider.kakao.admin-key}")
    private String kakaoAdminKey;

    //accessToken, refreshToken 발급
    @Transactional
    public LoginResponseDto createToken(Member member) {
        String newAccessToken = jwtService.generateAccessToken(member.getMemberId());
        String newRefreshToken = jwtService.generateRefreshToken(member.getMemberId());

        // DB에 refreshToken 저장
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    // refreshToken으로 accessToken 발급하기
    @Transactional
    public LoginResponseDto regenerateAccessToken(String refreshToken) {
        // refresh token 유효성 검사
        if (!jwtService.validateTokenBoolean(refreshToken))
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        Optional<Member> getMember = memberRepository.findByRefreshToken(refreshToken);
        if (getMember.isEmpty())
            throw new ExceptionHandler(MEMBER_NOT_FOUND);

        Member member = getMember.get();
        if (!refreshToken.equals(member.getRefreshToken()))
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        String newRefreshToken = jwtService.generateRefreshToken(member.getMemberId());
        String newAccessToken = jwtService.generateAccessToken(member.getMemberId());

        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    //애플 로그인
    @Transactional
    public LoginResponseDto appleLogin(AppleLoginRequestDto appleLoginRequestDto) {
        Optional<Member> memberByEmail = memberRepository.findByEmail(appleLoginRequestDto.getEmail());

        if (memberByEmail.isPresent()) {
            Member member = memberByEmail.get();
            // 이미 다른 소셜로 가입된 이메일인 경우
            if (!member.getLoginType().equals(LoginType.APPLE)) {
                throw new MemberHandler(MEMBER_EXIST_IN_OTHER_SOCIAL);
            }

            //애플 회원인 경우
            // 1. 애플 공개 키 가져오기
            Map<String, String> headers = jwtService.parseHeader(appleLoginRequestDto.getIdentityToken());
            PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

            // 2. JWT 검증 및 클레임 추출
            Claims claims = jwtService.getTokenClaims(appleLoginRequestDto.getIdentityToken(), publicKey);

            // 3. 클레임에서 subject 추출
            String sub = claims.getSubject();

            // 4. 유저가 등록되어 있는지 확인
            Member findMember = memberRepository.findByAppleSub(sub)
                    .orElse(null);

            if (findMember == null) {
                throw new MemberHandler(MEMBER_NOT_REGISTERED);  //회원가입
            }

            // 5. 토큰 생성 및 반환
            return createToken(findMember);
        }
        throw new MemberHandler(MEMBER_NOT_REGISTERED);  //회원가입
    }

    // 애플 회원가입
    @Transactional
    public LoginResponseDto appleSignUp(AppleSignUpRequestDto appleSignUpRequestDto) {
        Optional<Member> memberByEmail = memberRepository.findByEmail(appleSignUpRequestDto.getEmail());

        if (memberByEmail.isPresent()) {
            Member member = memberByEmail.get();
            // 이미 다른 소셜로 가입된 이메일인 경우
            if (!member.getLoginType().equals(LoginType.APPLE)) {
                throw new MemberHandler(MEMBER_EXIST_IN_OTHER_SOCIAL);
            }
            // 애플로 가입된 이메일인 경우
            throw new MemberHandler(MEMBER_ALREADY_EXIST);
        }

        Map<String, String> headers = jwtService.parseHeader(appleSignUpRequestDto.getIdentityToken());
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        Claims claims = jwtService.getTokenClaims(appleSignUpRequestDto.getIdentityToken(), publicKey);
        String sub = claims.getSubject();

        Member member = memberRepository.findByAppleSub(sub).orElse(null);

        if (member == null) {
            member = memberRepository.save(
                    MemberConverter.toAppleMember(sub, appleSignUpRequestDto)
            );
        }
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

    @Transactional
    public LoginResponseDto kakaoLogin(KakaoLoginRequestDTO kakaoReqDto) {
        // 카카오 액세스 토큰 검증
        KakaoUserDTO kakaoUser = kakaoService.validateKakaoToken(kakaoReqDto.getAccessToken());

        Optional<Member> memberByEmail = memberRepository.findByEmail(kakaoReqDto.getEmail());

        if (memberByEmail.isPresent()) {
            Member member = memberByEmail.get();
            // 이미 다른 소셜로 가입된 이메일인 경우
            if (!member.getLoginType().equals(LoginType.KAKAO)) {
                throw new MemberHandler(MEMBER_EXIST_IN_OTHER_SOCIAL);
            }
            // 카카오로 가입된 이메일인 경우
            if (!member.getKakaoUserId().equals(kakaoUser.getId())) {
                    throw new MemberHandler(MEMBER_NOT_FOUND);
                }
                // 로그인
                return createToken(member);
            }
        //등록된 이메일이 아닌 경우
        throw new MemberHandler(MEMBER_NOT_REGISTERED);
    }

    @Transactional
    public LoginResponseDto kakaoSignUp(KakaoSignUpRequestDTO kakaoSignUpReqDto) {

        // 카카오 액세스 토큰 검증
        KakaoUserDTO kakaoUser = kakaoService.validateKakaoToken(kakaoSignUpReqDto.getAccessToken());

        Optional<Member> memberByEmail = memberRepository.findByEmail(kakaoSignUpReqDto.getEmail());

        if (memberByEmail.isPresent()) {
            Member member = memberByEmail.get();
            // 이미 다른 소셜로 가입된 이메일인 경우
            if (!member.getLoginType().equals(LoginType.KAKAO)) {
                throw new MemberHandler(MEMBER_EXIST_IN_OTHER_SOCIAL);
            }
            // 카카오로 가입된 이메일인 경우
            throw new MemberHandler(MEMBER_ALREADY_EXIST);
        }

        //회원가입
        Member signUpMember = memberRepository.save(MemberConverter.toKakaoMember(kakaoUser, kakaoSignUpReqDto));
        return createToken(signUpMember);
    }

    public void kakaoDelete(Member member) {
        kakaoUnlinkClient.unlinkUser("KakaoAK " + kakaoAdminKey, "user_id", member.getKakaoUserId());
        memberService.deleteMember(member);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!jwtService.validateTokenBoolean(refreshToken))  // refresh token 유효성 검사
            throw new ExceptionHandler(REFRESH_TOKEN_UNAUTHORIZED);

        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ExceptionHandler(MEMBER_NOT_FOUND)); // 예외처리

        member.refreshTokenExpires();
        memberRepository.save(member);
    }
}