package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoService todoService;
    private final UserService userService;

    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        // 일정을 만든 유저
        User user = User.fromAuthUser(authUser);
        Todo todo = todoService.findByIdOrFail(todoId);
        todo.validateTodoOwner(user);
        //일정 작성자는 자신을 담당자로 지정 불가능
        todo.validateNotSelfAssignment(managerSaveRequest.getManagerUserId());
        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_ASSIGNMENT_TARGET_USER_NOT_FOUND));
        Manager savedManagerUser = managerRepository.save(new Manager(managerUser, todo));

        return new ManagerSaveResponse(
                savedManagerUser.getId(),
                new UserResponse(managerUser)
        );
    }

    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoService.findByIdOrFail(todoId);
        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        return managerList.stream().map(manager -> {
            User user = manager.getUser();
            return new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user));
        }).toList();
    }

    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        Todo todo = todoService.findByIdOrFail(todoId);
        todo.validateTodoOwner(User.fromAuthUser(authUser));
        Manager manager = findByIdOrFail(managerId);
        manager.validateBelongsTodo(todo);
        managerRepository.delete(manager);
    }

    public Manager findByIdOrFail(Long managerId) {
        return managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.MANAGER_NOT_FOUND));
    }
}
