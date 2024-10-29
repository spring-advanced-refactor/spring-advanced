package org.example.expert.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.service.comment.CommentAdminService;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @DeleteMapping("/admin/comments/{commentId}")
    public ResponseEntity<ApiResult<String>> deleteComment(@Auth AuthUser authUser, @PathVariable(value = "commentId") Long commentId) {
        commentAdminService.deleteComment(authUser, commentId);
        return ResponseEntity.ok(ApiResult.success("Comment has been successfully deleted"));
    }
}
