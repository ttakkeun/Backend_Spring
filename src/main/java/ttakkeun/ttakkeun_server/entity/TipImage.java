package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TipImage extends BaseEntity {

    @Id
    @Column(name = "tip_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tipImageId;

    @Column(nullable = false)
    private String tipImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_id")
    private Tip tip;

}

