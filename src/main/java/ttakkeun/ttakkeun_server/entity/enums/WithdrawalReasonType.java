package ttakkeun.ttakkeun_server.entity.enums;

public enum WithdrawalReasonType {
    FEATURE_DISSATISFACTION("기능 불만족"),
    LACK_OF_CONTENT("앱 내 콘텐츠 부족"),
    TECHNICAL_ISSUES("기술적 문제 (버그, 오류)"),
    COMPATIBILITY_ISSUES("기기 호환성 문제"),
    UX_INCONVENIENCE("사용자 경험 불편"),
    OTHER("기타 사유");

    private final String description;

    WithdrawalReasonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
