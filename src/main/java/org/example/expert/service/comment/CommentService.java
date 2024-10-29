package org.example.expert.service.comment;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.auth.RequireAuthenticatedUser;
import org.example.expert.domain.comment.Comment;
import org.example.expert.domain.comment.CommentRepository;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.comment.request.CommentSaveRequest;
import org.example.expert.dto.comment.response.CommentResponse;
import org.example.expert.dto.comment.response.CommentSaveResponse;
import org.example.expert.dto.user.response.UserResponse;
import org.example.expert.service.TodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoService todoService;

    @Transactional
    @RequireAuthenticatedUser(requireRole = UserRole.ADMIN)
    public CommentSaveResponse saveComment(AuthUser authUser, Long todoId, CommentSaveRequest commentSaveRequest) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoService.findByIdOrFail(todoId);
        Comment savedComment = commentRepository.save(commentSaveRequest.toEntity(user, todo));

        return new CommentSaveResponse(savedComment, new UserResponse(user));
    }

    public List<CommentResponse> getComments(Long todoId) {
        return commentRepository.findByTodoIdWithUser(todoId).stream()
                .map(comment -> new CommentResponse(comment, new UserResponse(comment.getUser())))
                .toList();
    }
}
