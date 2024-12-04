package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Record extends BaseEntity {

    @Id
    @Column(name = "record_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    private String etc; //기타 사항 입력

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<UserAnswer> answerList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Result> results = new ArrayList<>();
}
