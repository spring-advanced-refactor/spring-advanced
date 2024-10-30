package org.example.expert.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.todo.request.TodoSaveRequest;
import org.example.expert.dto.todo.response.TodoResponse;
import org.example.expert.dto.todo.response.TodoSaveResponse;
import org.example.expert.service.TodoService;
import org.example.expert.util.api.ApiResult;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<ApiResult<TodoSaveResponse>> saveTodo(
            @Auth AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(ApiResult.success(todoService.saveTodo(authUser, todoSaveRequest)));
    }

    @GetMapping("/todos")
    public ResponseEntity<ApiResult<Page<TodoResponse>>> getTodos(
            @RequestParam(defaultValue = "1", value = "page") @Positive int page,
            @RequestParam(defaultValue = "10", value = "size") @Positive int size
    ) {
        return ResponseEntity.ok(ApiResult.success(todoService.getTodos(page, size)));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<ApiResult<TodoResponse>> getTodo(@PathVariable(value = "todoId") Long todoId) {
        return ResponseEntity.ok(ApiResult.success(todoService.getTodo(todoId)));
    }
}
