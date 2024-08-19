package ttakkeun.ttakkeun_server.dto.tip;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeTipResponseDTO {
    private int totalLikes;
    private boolean isLike;
}
