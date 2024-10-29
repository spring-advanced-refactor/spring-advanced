package org.example.expert.domain.comment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.Timestamped;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.user.User;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @Builder
    public Comment(String contents, User user, Todo todo) {
        this.contents = contents;
        this.user = user;
        this.todo = todo;
    }

    public void update(String contents) {
        this.contents = contents;
    }
}
