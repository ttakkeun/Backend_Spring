package ttakkeun.ttakkeun_server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDTO {
    private Boolean isSuccess;
    private int code;
    private String message;
    private Result result;

    @Getter
    @Builder
    public static class Result {
        private Integer totalLikes;
        private Boolean isLike;
    }
}
