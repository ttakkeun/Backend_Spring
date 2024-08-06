package ttakkeun.ttakkeun_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.*;
import ttakkeun.ttakkeun_server.service.TodoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ApiResponse<TodoResponseDto> createTodo(
            // @RequestHeader("Authorization") String accessToken,
            @RequestBody TodoCreateRequestDto todoCreateRequestDto) {
        TodoResponseDto result = todoService.createTodo(todoCreateRequestDto);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{todoId}/check")
    public ApiResponse<TodoResponseDto> updateTodoStatus(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId,
            @RequestBody TodoStatusUpdateRequestDto requestDto) {
        TodoResponseDto result = todoService.updateTodoStatus(todoId, requestDto);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{todoId}")
    public ApiResponse<TodoResponseDto> updateTodoContent(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId,
            @RequestBody TodoContentUpdateRequestDto requestDto) {
        TodoResponseDto result = todoService.updateTodoContent(todoId, requestDto);
        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/{todoId}")
    public ApiResponse<TodoDeleteResponseDto> deleteTodo(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId) {
        TodoDeleteResponseDto result = todoService.deleteTodo(todoId);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/{todoId}/repeat-tomorrow")
    public ApiResponse<TodoResponseDto> repeatTodoTomorrow(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId) {
        TodoResponseDto result = todoService.repeatTodoTomorrow(todoId);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{todoId}/do-tomorrow")
    public ApiResponse<TodoResponseDto> doTomorrow(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId) {
        TodoResponseDto result = todoService.doTomorrow(todoId);
        return ApiResponse.onSuccess(result);
    }


    @PostMapping("/{todoId}/repeat-another-day")
    public ApiResponse<TodoResponseDto> repeatAnotherDay(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId,
            @RequestBody RepeatAnotherDayRequestDto requestDto) {
        TodoResponseDto result = todoService.repeatAnotherDay(todoId, requestDto);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{todoId}/change-date")
    public ApiResponse<TodoResponseDto> changeDate(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todoId,
            @RequestBody ChangeDateRequestDto requestDto) {
        TodoResponseDto result = todoService.changeDate(todoId, requestDto);
        return ApiResponse.onSuccess(result);
    }
}
