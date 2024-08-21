package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long todoId;
    private String todoName;
    private Boolean todoStatus;

}
