package org.example.expert.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.auth.RequireAuthenticatedUser;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.todo.TodoRepository;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.todo.request.TodoSaveRequest;
import org.example.expert.dto.todo.response.TodoResponse;
import org.example.expert.dto.todo.response.TodoSaveResponse;
import org.example.expert.dto.user.response.UserResponse;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    public Todo findByIdOrFail(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));
    }

    @Transactional
    @RequireAuthenticatedUser
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);
        String weather = weatherClient.getTodayWeather();
        Todo savedTodo = todoRepository.save(todoSaveRequest.toEntity(user, weather));

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user)
        );
    }

    public Page<TodoResponse> getTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(todo -> new TodoResponse(todo, new UserResponse(todo.getUser())));
    }

    public TodoResponse getTodo(Long todoId) {
        Todo todo = findByIdOrFail(todoId);
        User user = todo.getUser();

        return new TodoResponse(todo, new UserResponse(user));
    }
}
