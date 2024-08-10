package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
