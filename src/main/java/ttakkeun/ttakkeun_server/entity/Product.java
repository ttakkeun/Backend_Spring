package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ResultProduct> resultProductList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<LikeProduct> likeProductList = new ArrayList<>();

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

    //좋아요 수
    @Formula("(SELECT COUNT(lp.member_id) FROM like_product lp WHERE lp.product_id = product_id)")
    private int totalLikes;


}
