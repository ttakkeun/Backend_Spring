package ttakkeun.ttakkeun_server.converter;

import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.auth.apple.AppleSignUpRequestDto;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoSignUpRequestDTO;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoUserDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.LoginType;

import java.time.LocalDateTime;

@Component
public class MemberConverter {
    public static Member toAppleMember(String sub, AppleSignUpRequestDto appleSignUpRequestDto) {
        return Member.builder()
                .email(appleSignUpRequestDto.getEmail())
                .username(appleSignUpRequestDto.getName()) // 닉네임 가져오기
                .appleSub(sub)
                .loginType(LoginType.APPLE)
                .refreshToken("") // 초기 빈 값 설정
                .refreshTokenExpiresAt(LocalDateTime.now()) // 초기 시간 설정
                .build();
    }

    public static Member toKakaoMember(KakaoUserDTO kakaoUserDTO, KakaoSignUpRequestDTO kakaoSignUpRequestDTO) {
        return Member.builder()
                .email(kakaoSignUpRequestDTO.getEmail())
                .loginType(LoginType.KAKAO)
                .kakaoUserId(kakaoUserDTO.getId())
                .username(kakaoSignUpRequestDTO.getName())
                .refreshToken("") // 초기 빈 값 설정
                .refreshTokenExpiresAt(LocalDateTime.now()) // 초기 시간 설정
                .build();
    }
}
