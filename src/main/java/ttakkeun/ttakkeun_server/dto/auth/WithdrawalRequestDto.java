package ttakkeun.ttakkeun_server.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ttakkeun.ttakkeun_server.entity.enums.WithdrawalReasonType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequestDto {

    @NotNull(message = "탈퇴 사유는 필수입니다")
    private WithdrawalReasonType reasonType;

    @Size(max = 500, message = "기타 사유는 500자 이내로 입력해주세요")
    private String customReason;

    // 기타 사유 유효성 검증
    public boolean isValid() {
        if (reasonType == WithdrawalReasonType.OTHER) {
            return customReason != null && !customReason.trim().isEmpty();
        }
        return true;
    }
}
