package ttakkeun.ttakkeun_server.dto.product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestDTO {
    String title;
    String image;
    int price;
    String brand;
    String link;
    String category1;
    String category2;
    String category3;
    String category4;
}
