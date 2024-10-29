package org.example.expert.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.Comment;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveRequest {

    @NotBlank
    private String contents;

    public Comment toEntity(User user, Todo todo) {
        return Comment.builder()
                .contents(this.contents)
                .user(user)
                .todo(todo)
                .build();
    }
}
