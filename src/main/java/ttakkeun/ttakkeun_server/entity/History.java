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
public class History extends BaseEntity {

    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Enumerated(EnumType.STRING)
    private Category historyCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

}
