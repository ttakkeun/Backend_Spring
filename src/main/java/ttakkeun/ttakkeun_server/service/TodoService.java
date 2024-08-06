package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.dto.*;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                .todoStatus(request.getTodoStatus())
                .petId(pet)
                .createdAt(request.getCreatedAt())
                .build();

        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt());
    }

    @Transactional
    public TodoResponseDto updateTodoStatus(Long todoId, TodoStatusUpdateRequestDto request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        todo.setTodoStatus(request.getTodoStatus());
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt());
    }

    @Transactional
    public TodoResponseDto updateTodoContent(Long todoId, TodoContentUpdateRequestDto request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        todo.setTodoCategory(request.getTodoCategory());
        todo.setTodoName(request.getTodoName());
        todo.setTodoStatus(request.getTodoStatus());
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt());
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
        // 1. 원래 투두 항목을 조회
        Todo originalTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        // 2. 투두 항목이 완료된 상태인지 확인
        if (originalTodo.getTodoStatus() != TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 완료되지 않았습니다.");
        }

        // 3. 새로운 투두 항목을 생성
        Todo newTodo = Todo.builder()
                .todoName(originalTodo.getTodoName())
                .todoCategory(originalTodo.getTodoCategory())
                .todoStatus(TodoStatus.ONPROGRESS) // 새로운 투두 항목은 완료되지 않은 상태로 생성
                .petId(originalTodo.getPetId())
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        // 4. 새로운 투두 항목을 저장
        todoRepository.save(newTodo);

        // 5. 응답 DTO를 반환
        return new TodoResponseDto(newTodo.getTodoId(), newTodo.getCreatedAt());
    }

    @Transactional
    public TodoResponseDto doTomorrow(Long todoId) {
        // 1. 원래 투두 항목을 조회
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        // 2. 투두 항목이 완료되지 않은 상태인지 확인
        if (todo.getTodoStatus() == TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 이미 완료된 상태입니다.");
        }

        // 3. 투두 항목의 날짜를 내일로 변경
        todo.setCreatedAt(LocalDateTime.now().plusDays(1));

        // 4. 변경된 투두 항목을 저장
        todoRepository.save(todo);

        // 5. 응답 DTO를 반환
        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt());
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
                .petId(originalTodo.getPetId())
                .createdAt(newDate)
                .build();

        todoRepository.save(newTodo);

        return new TodoResponseDto(newTodo.getTodoId(), newTodo.getCreatedAt());
    }

    @Transactional
    public TodoResponseDto changeDate(Long todoId, ChangeDateRequestDto requestDto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Todo ID입니다."));

        if (todo.getTodoStatus() != TodoStatus.DONE) {
            throw new IllegalStateException("투두 항목이 완료되지 않았습니다.");
        }

        LocalDateTime newDate = requestDto.getNewDate();
        todo.setCreatedAt(newDate);
        todoRepository.save(todo);

        return new TodoResponseDto(todo.getTodoId(), todo.getCreatedAt());
    }
}
