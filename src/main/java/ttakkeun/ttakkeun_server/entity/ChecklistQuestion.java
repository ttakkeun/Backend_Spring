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
public class ChecklistQuestion extends BaseEntity {

    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    private Category questionCategory;

    private String questionText;

    private String descriptionText;

    private Boolean isDupe;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<UserAnswer> answerList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "checklistQuestion", cascade = CascadeType.ALL)
    private List<ChecklistAnswer> checklistAnswers = new ArrayList<>();

}
