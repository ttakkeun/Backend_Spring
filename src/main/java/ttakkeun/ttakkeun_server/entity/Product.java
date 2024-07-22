package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productTitle;
    private String productLink;
    private String productImage;

    private Integer lprice;

    private String brand;

    private String category1;
    private String category2;
    private String category3;
    private String category4;

    @Enumerated(EnumType.STRING)
    private Category tag;
}
