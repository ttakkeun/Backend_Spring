package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChecklistAnswer extends BaseEntity {

    @Id
    @Column(name = "anwser_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    private String answerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private ChecklistQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    @Builder.Default
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<Image> imageList = new ArrayList<>();

}
