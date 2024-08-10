package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.Category;

@Getter
@Setter
@AllArgsConstructor
public class TodoCreateRequestDto {
    private Long petId;
    private Category todoCategory;
    private String todoName;
}


