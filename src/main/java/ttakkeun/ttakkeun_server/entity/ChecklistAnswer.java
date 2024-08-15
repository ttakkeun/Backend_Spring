package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChecklistAnswer extends BaseEntity {

    @Id
    @Column(name = "checklist_answer_id")
    private Long checklistAnswerId;

    private String checklistAnswerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private ChecklistQuestion checklistQuestion;


}
