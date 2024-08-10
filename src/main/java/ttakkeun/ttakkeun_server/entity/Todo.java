package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Todo extends BaseEntity {

    @Id
    @Column(name = "todo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    private String todoName;

    @Enumerated(EnumType.STRING)
    private Category todoCategory;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TodoStatus todoStatus = TodoStatus.ONPROGRESS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet petId;

    @Builder.Default
    @OneToMany(mappedBy = "todoId", cascade = CascadeType.ALL)
    private List<History> HistoryList = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public Todo(String todoName, Category todoCategory, TodoStatus todoStatus, Pet petId) {
        this.todoName = todoName;
        this.todoCategory = todoCategory;
        this.todoStatus = todoStatus;
        this.petId = petId;
    }
}