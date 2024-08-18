package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.converter.StringListConverter;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAnswer extends BaseEntity {

    @Id
    @Column(name = "user_anwser_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAnswerId;

    @Convert(converter = StringListConverter.class)
    private List<String> userAnswerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private ChecklistQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    @Builder.Default
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    public void addImages(List<Image> images) {
        for (Image image : images) {
            image.setAnswer(this);
            this.images.add(image);
        }
    }

}
