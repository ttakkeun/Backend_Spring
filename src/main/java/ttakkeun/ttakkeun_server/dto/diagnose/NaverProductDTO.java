package ttakkeun.ttakkeun_server.dto.diagnose;

import lombok.Builder;
import ttakkeun.ttakkeun_server.entity.enums.Category;

@Builder
public record NaverProductDTO(Long productId, String title, String link, String image, Integer lprice, String mall_name,
                              String brand, String category1, String category2, String category3, String category4, Category tag) {
}