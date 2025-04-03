package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long todoId;
    private String todoName;
    private Boolean todoStatus;

}
