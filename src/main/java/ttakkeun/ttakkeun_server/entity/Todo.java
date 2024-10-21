package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

import java.time.LocalDate;
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
    private Pet pet;

    private LocalDate todoDate;

    public Todo(String todoName, Category todoCategory, TodoStatus todoStatus, Pet pet) {
        this.todoName = todoName;
        this.todoCategory = todoCategory;
        this.todoStatus = todoStatus;
        this.pet = pet;
    }
}