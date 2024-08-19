package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.todo.*;
import ttakkeun.ttakkeun_server.service.TodoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "투두 생성 API")
    @PostMapping
    public ApiResponse<TodoResponseDto> createTodo(
            // @RequestHeader("Authorization") String accessToken,
            @RequestBody TodoCreateRequestDto todoCreateRequestDto) {
        TodoResponseDto result = todoService.createTodo(todoCreateRequestDto);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 체크/취소 업데이트 API")
    @PatchMapping("/{todo_id}/check")
    public ApiResponse<TodoResponseDto> updateTodoStatus(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id) {
        TodoResponseDto result = todoService.updateTodoStatus(todo_id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 수정 API")
    @PatchMapping("/{todo_id}")
    public ApiResponse<TodoResponseDto> updateTodoContent(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id,
            @RequestBody TodoContentUpdateRequestDto requestDto) {
        TodoResponseDto result = todoService.updateTodoContent(todo_id, requestDto);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 삭제 API")
    @DeleteMapping("/{todo_id}")
    public ApiResponse<TodoDeleteResponseDto> deleteTodo(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id) {
        TodoDeleteResponseDto result = todoService.deleteTodo(todo_id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 내일 또 하기 API")
    @PostMapping("/{todo_id}/repeat-tomorrow")
    public ApiResponse<TodoResponseDto> repeatTodoTomorrow(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id) {
        TodoResponseDto result = todoService.repeatTodoTomorrow(todo_id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 내일하기 API")
    @PatchMapping("/{todo_id}/do-tomorrow")
    public ApiResponse<TodoResponseDto> doTomorrow(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id) {
        TodoResponseDto result = todoService.doTomorrow(todo_id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 다른 날 또 하기 API")
    @PostMapping("/{todo_id}/repeat-another-day")
    public ApiResponse<TodoResponseDto> repeatAnotherDay(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id,
            @RequestBody RepeatAnotherDayRequestDto requestDto) {
        TodoResponseDto result = todoService.repeatAnotherDay(todo_id, requestDto);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "투두 날짜 바꾸기 API")
    @PatchMapping("/{todo_id}/change-date")
    public ApiResponse<TodoResponseDto> changeDate(
            // @RequestHeader("Authorization") String accessToken,
            @PathVariable Long todo_id,
            @RequestBody ChangeDateRequestDto requestDto) {
        TodoResponseDto result = todoService.changeDate(todo_id, requestDto);
        return ApiResponse.onSuccess(result);
    }
}
