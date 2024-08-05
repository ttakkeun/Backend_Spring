package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.dto.*;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

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

}
