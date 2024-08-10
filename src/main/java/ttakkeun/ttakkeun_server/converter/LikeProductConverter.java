package ttakkeun.ttakkeun_server.converter;

import ttakkeun.ttakkeun_server.dto.LikeResponseDTO;

public class LikeProductConverter {
    public static LikeResponseDTO.Result toDTO(int totalLikes, boolean isLike) {
        LikeResponseDTO.Result result = LikeResponseDTO.Result.builder()
                .totalLikes(totalLikes)
                .isLike(isLike)
                .build();

        return result;
    }
}
