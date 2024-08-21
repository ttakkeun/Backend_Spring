package ttakkeun.ttakkeun_server.service;

import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.todo.CalendarResponseDto;
import ttakkeun.ttakkeun_server.dto.todo.TodoDto;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final TodoRepository todoRepository;
    private final PetRepository petRepository;

    public CalendarService(TodoRepository todoRepository, PetRepository petRepository) {

        this.todoRepository = todoRepository;
        this.petRepository = petRepository;
    }

    public CalendarResponseDto getCalendarData(Long petId, int year, int month, int date) {
        LocalDate selectDate = LocalDate.of(year, month, date);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found with id: " + petId));


        List<Todo> todos = todoRepository.findByTodoDateAndPet(selectDate, pet);

        // 투두 항목별로 분류하기
        List<TodoDto> earTodos = filterTodosByCategory(todos, Category.EAR);
        List<TodoDto> hairTodos = filterTodosByCategory(todos, Category.HAIR);
        List<TodoDto> clawTodos = filterTodosByCategory(todos, Category.CLAW);
        List<TodoDto> eyeTodos = filterTodosByCategory(todos, Category.EYE);
        List<TodoDto> teethTodos = filterTodosByCategory(todos, Category.TEETH);

        return new CalendarResponseDto(
                selectDate,
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
