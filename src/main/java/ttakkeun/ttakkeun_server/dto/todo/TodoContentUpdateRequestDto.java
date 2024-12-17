package ttakkeun.ttakkeun_server.dto.todo;

import lombok.Getter;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

@Getter
@Setter
public class TodoContentUpdateRequestDto {
    private String todoName;
}
