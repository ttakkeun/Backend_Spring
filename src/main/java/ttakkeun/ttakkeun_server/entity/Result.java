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
public class Result extends BaseEntity {

    @Id
    @Column(name = "result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    // Result 객체를 로드할 때 ProductList도 즉시 가져오도록 EAGER로 설정
    @Builder.Default
    @OneToMany(mappedBy = "result", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ResultProduct> ProductList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<Record> RecordList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Category resultCategory;

    private Integer score;

    private String resultDetail;

    private String resultCare;
}
