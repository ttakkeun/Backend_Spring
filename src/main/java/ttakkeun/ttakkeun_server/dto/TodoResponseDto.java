package ttakkeun.ttakkeun_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private String createdAt;
}
