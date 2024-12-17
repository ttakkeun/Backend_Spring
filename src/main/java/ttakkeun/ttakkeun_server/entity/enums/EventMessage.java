package ttakkeun.ttakkeun_server.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EventMessage {
    SIGN_UP_EVENT("[신규회원]따끈의 " + "kakao, apple"+ "N" + "번째 유저 00님이 가입했어요. 👶🏻"),
    POINT_USE_EVENT("[포인트]포인트를 사용했어요"),
    INQUIRY_EVENT("[문의하기]ㅇㅇ님의 문의"),
    REPORT_EVENT("[신고하기]00님이 신고당했어요");

    private final String message;

    public static String signUpMessage(LoginType loginType, Long memberId, String userName) {
        return String.format("[신규회원] 따끈의 %s %d번째 유저 %s님이 가입했어요. 👶🏻", loginType, memberId, userName);
    }
}

