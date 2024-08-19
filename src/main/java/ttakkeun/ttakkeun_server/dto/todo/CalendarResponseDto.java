package ttakkeun.ttakkeun_server.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CalendarResponseDto {
    private LocalDate date;
    // private List<TodoDto> todos;
    private List<TodoDto> earTodos;
    private List<TodoDto> hairTodos;
    private List<TodoDto> clawTodos;
    private List<TodoDto> eyeTodos;
    private List<TodoDto> teethTodos;
}
