package org.example.expert.dto.todo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.user.User;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSaveRequest {

    @NotBlank
    @Length(min = 1, max = 127)
    private String title;
    @NotBlank
    @Length(min = 1, max = 512)
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
