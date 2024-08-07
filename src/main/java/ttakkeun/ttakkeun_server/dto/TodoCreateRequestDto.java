package ttakkeun.ttakkeun_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

@Getter
@Setter
@AllArgsConstructor
public class TodoCreateRequestDto {
    private Long petId;
    private Category todoCategory;
    private String todoName;
    private TodoStatus todoStatus;
    private String createdAt;
}
