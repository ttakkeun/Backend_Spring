package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tip extends BaseEntity {

    @Id
    @Column(name = "tip_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tipId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Formula("(SELECT COUNT(lt.member_id) FROM like_tip lt WHERE lt.tip_id = tip_id)")
    private Integer recommendCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "tip", cascade = CascadeType.ALL)
    private List<TipImage> images = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean isPopular = false;

    public void addImage(TipImage image) {
        image.setTip(this);
        this.images.add(image);
    }

}
