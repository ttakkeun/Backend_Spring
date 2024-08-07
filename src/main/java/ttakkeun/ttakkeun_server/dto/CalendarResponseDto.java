package ttakkeun.ttakkeun_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CalendarResponseDto {
    private String date;
    // private List<TodoDto> todos;
    private List<TodoDto> earTodos;
    private List<TodoDto> hairTodos;
    private List<TodoDto> clawTodos;
    private List<TodoDto> eyeTodos;
    private List<TodoDto> teethTodos;
}
