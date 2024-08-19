package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.dto.todo.*;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final PetRepository petRepository;

    @Transactional
    public TodoResponseDto createTodo(TodoCreateRequestDto request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않는 Pet Id 입니다"));

        Todo todo = Todo.builder()
                .todoName(request.getTodoName())
                .todoCategory(request.getTodoCategory())
                .pet(pet)
                .build();

        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt(), null);
    }

    @Transactional
    public TodoResponseDto updateTodoStatus(Long todoId, TodoStatusUpdateRequestDto request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        todo.setTodoStatus(request.getTodoStatus());
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt(), null);
    }

    @Transactional
    public TodoResponseDto updateTodoContent(Long todoId, TodoContentUpdateRequestDto request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        todo.setTodoCategory(request.getTodoCategory());
        todo.setTodoName(request.getTodoName());
        todo.setTodoStatus(request.getTodoStatus());
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt(), null);
    }

    @Transactional
    public TodoDeleteResponseDto deleteTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        todoRepository.delete(todo);

        return new TodoDeleteResponseDto(todo.getTodoId());
    }

    @Transactional
    public TodoResponseDto repeatTodoTomorrow(Long todoId) {
        Todo originalTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        if (originalTodo.getTodoStatus() != TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 완료되지 않았습니다.");
        }

        Todo newTodo = Todo.builder()
                .todoName(originalTodo.getTodoName())
                .todoCategory(originalTodo.getTodoCategory())
                .todoStatus(TodoStatus.ONPROGRESS) // 새로운 투두 항목은 완료되지 않은 상태로 생성
                .pet(originalTodo.getPet())
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        todoRepository.save(newTodo);

        return new TodoResponseDto(newTodo.getTodoId(), newTodo.getCreatedAt(), null);
    }

    @Transactional
    public TodoResponseDto doTomorrow(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        if (todo.getTodoStatus() == TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 이미 완료된 상태입니다.");
        }

        todo.setUpdatedAt(LocalDateTime.now().plusDays(1));

        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), null, todo.getUpdatedAt());
    }

    @Transactional
    public TodoResponseDto repeatAnotherDay(Long todoId, RepeatAnotherDayRequestDto requestDto) {
        Todo originalTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        if (originalTodo.getTodoStatus() != TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 완료되지 않았습니다.");
        }

        LocalDateTime newDate = requestDto.getNewDate();

        Todo newTodo = Todo.builder()
                .todoName(originalTodo.getTodoName())
                .todoCategory(originalTodo.getTodoCategory())
                .todoStatus(TodoStatus.ONPROGRESS)
                .pet(originalTodo.getPet())
                .createdAt(newDate)
                .build();

        todoRepository.save(newTodo);

        return new TodoResponseDto(newTodo.getTodoId(), newTodo.getCreatedAt(), null);
    }

    @Transactional
    public TodoResponseDto changeDate(Long todoId, ChangeDateRequestDto requestDto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        if (todo.getTodoStatus() == TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 이미 완료된 상태입니다.");
        }

        LocalDateTime newDate = requestDto.getNewDate();
        todo.setUpdatedAt(newDate);
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), null, todo.getUpdatedAt());
    }
}
