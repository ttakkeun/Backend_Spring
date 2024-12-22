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
        return String.format("[신규회원] 따끈의 %s %d번째 유저 %s님이 가입했어요.", loginType, memberId, userName);
    }
    public static String pointMessage(String userName, Long usedPoint, Integer remainPoint) {
        return String.format("[포인트] %s님이 %d포인트를 사용했습니다!\n" +
                "남은 포인트: %d", userName, usedPoint, remainPoint);
    }
    public static String inquiryMessage(String userName, String inquiryText) {
        return String.format("[문의하기] %s님이 문의를 남겼습니다.\n" +
                "내용: %s", userName, inquiryText);
    }
    public static String reportMessage(String userName, String reportUser, Long tipId, String reportText) {
        return String.format("[신고하기] %s님이 %s님의 게시글 %d를 신고했습니다.\n" +
                "신고 사유: %s", userName, reportUser, tipId, reportText);
    }
}

