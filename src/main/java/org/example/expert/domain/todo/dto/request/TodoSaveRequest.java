package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSaveRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String contents;

    public Todo toEntity(User user, String weather) {
        return Todo.builder()
                .user(user)
                .contents(this.contents)
                .title(this.title)
                .weather(weather)
                .build();
    }
}
