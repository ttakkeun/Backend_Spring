package ttakkeun.ttakkeun_server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDTO {
    private Long product_id;
    private String title;
    private String image;
    private Integer price;
    private String brand;
    private String link;
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    private Integer total_likes;
    private Boolean isLike;
}
