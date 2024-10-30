package org.example.expert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.comment.request.CommentSaveRequest;
import org.example.expert.dto.comment.response.CommentResponse;
import org.example.expert.dto.comment.response.CommentSaveResponse;
import org.example.expert.service.comment.CommentService;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<ApiResult<CommentSaveResponse>> saveComment(
            @Auth AuthUser authUser,
            @PathVariable(value = "todoId") Long todoId,
            @Valid @RequestBody CommentSaveRequest commentSaveRequest
    ) {
        return ResponseEntity.ok(ApiResult.success(commentService.saveComment(authUser, todoId, commentSaveRequest)));
    }

    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<ApiResult<List<CommentResponse>>> getComments(@PathVariable Long todoId) {
        return ResponseEntity.ok(ApiResult.success(commentService.getComments(todoId)));
    }
}
