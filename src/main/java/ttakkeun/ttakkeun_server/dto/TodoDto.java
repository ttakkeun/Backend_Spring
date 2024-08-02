package ttakkeun.ttakkeun_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long todoId;
    private String todoName;
    private TodoStatus todoStatus;

}
