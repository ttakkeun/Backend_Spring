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

    @OneToMany(mappedBy = "resultId", cascade = CascadeType.ALL)
    private List<Record> RecordList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Category resultCategory;

    private Integer score;

    private String resultDetail;

    private String resultCare;
}
