package ttakkeun.ttakkeun_server.service;

import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.todo.CalendarResponseDto;
import ttakkeun.ttakkeun_server.dto.todo.TodoDto;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final TodoRepository todoRepository;

    public CalendarService(TodoRepository todoRepository) {

        this.todoRepository = todoRepository;
    }

    public CalendarResponseDto getCalendarData(int year, int month, int date) {
        LocalDateTime start = LocalDateTime.of(year, month, date, 0, 0);
        LocalDateTime end = start.with(LocalTime.MAX);

        List<Todo> todos = todoRepository.findByCreatedAtBetween(start, end);

        // 투두 항목별로 분류하기
        List<TodoDto> earTodos = filterTodosByCategory(todos, Category.EAR);
        List<TodoDto> hairTodos = filterTodosByCategory(todos, Category.HAIR);
        List<TodoDto> clawTodos = filterTodosByCategory(todos, Category.CLAW);
        List<TodoDto> eyeTodos = filterTodosByCategory(todos, Category.EYE);
        List<TodoDto> teethTodos = filterTodosByCategory(todos, Category.TEETH);

        return new CalendarResponseDto(
                String.format("%04d-%02d-%02d", year, month, date),
                earTodos, hairTodos, clawTodos, eyeTodos, teethTodos
        );
    }

    private List<TodoDto> convertToDto(List<Todo> todos) {
        return todos.stream().map(todo -> new TodoDto(
                todo.getTodoId(),
                todo.getTodoName(),
                todo.getTodoStatus()
        )).collect(Collectors.toList());
    }

    private List<TodoDto> filterTodosByCategory(List<Todo> todos, Category todoCategory) {
        return todos.stream()
                .filter(todo -> todo.getTodoCategory().equals(todoCategory))
                .map(todo -> new TodoDto(
                        todo.getTodoId(),
                        todo.getTodoName(),
                        todo.getTodoStatus()
                ))
                .collect(Collectors.toList());
    }

}
