package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.enums.WithdrawalReasonType;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Withdrawal {

    @Id
    @Column(name = "withdrawal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long withdrawalId;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 탈퇴한 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false)
    private WithdrawalReasonType reasonType;

    @Column(name = "custom_reason", length = 500)
    private String customReason;    // reasonType이 OTHER일 때만 사용

    // 기타 사유인지 확인하는 헬퍼 메소드
    public boolean isOtherReason() {
        return reasonType == WithdrawalReasonType.OTHER;
    }

    // 표시용 사유 텍스트 반환
    public String getDisplayReason() {
        if (isOtherReason() && customReason != null && !customReason.trim().isEmpty()) {
            return customReason;
        }
        return reasonType.getDescription();
    }

}
