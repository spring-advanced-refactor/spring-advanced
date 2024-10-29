package org.example.expert.domain.todo;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.Comment;
import org.example.expert.domain.common.Timestamped;
import org.example.expert.domain.manager.Manager;
import org.example.expert.domain.user.User;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todos")
public class Todo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    private String weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "todo", cascade = CascadeType.PERSIST)
    private List<Manager> managers = new ArrayList<>();

    @Builder
    public Todo(String title, String contents, String weather, User user) {
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
        this.managers.add(new Manager(user, this));
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void validateTodoOwner(User user) {
        if (this.user == null) {
            throw new InvalidRequestException(ErrorCode.TODO_CREATOR_NOT_FOUND);
        }
        if (!ObjectUtils.nullSafeEquals(user.getId(), this.getUser().getId())) {
            throw new InvalidRequestException(ErrorCode.TODO_CREATOR_PERMISSION_DENIED);
        }
    }

    public void validateNotSelfAssignment(Long userId) {
        if (ObjectUtils.nullSafeEquals(this.user.getId(), userId)) {
            throw new InvalidRequestException(ErrorCode.TODO_CREATOR_SELF_ASSIGNMENT_INVALID);
        }
    }
}
