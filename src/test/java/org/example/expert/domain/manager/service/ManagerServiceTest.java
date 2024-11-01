package org.example.expert.domain.manager.service;

import org.example.expert.domain.manager.Manager;
import org.example.expert.domain.manager.ManagerRepository;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.todo.TodoRepository;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRepository;
import org.example.expert.domain.user.UserRole;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.manager.request.ManagerSaveRequest;
import org.example.expert.dto.manager.response.ManagerResponse;
import org.example.expert.dto.manager.response.ManagerSaveResponse;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.example.expert.service.ManagerService;
import org.example.expert.service.TodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoService todoService;
    @InjectMocks
    private ManagerService managerService;

    @Test
    @DisplayName("Manager 목록 조회 시 Todo가 없다면 예외 발생")
    public void getManagers_시_Todo가_없다면_예외가_발생한다() {
        // given
        long todoId = 1L;
        given(todoService.findByIdOrFail(todoId))
                .willThrow(new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class, () -> managerService.getManagers(todoId));

        assertAll(
                () -> assertEquals(ErrorCode.TODO_NOT_FOUND.getMsg(), exception.getMessage()),
                () -> verify(todoService, times(1)).findByIdOrFail(todoId)
        );
    }

    @Test
    @DisplayName("담당자 배정 시 Todo를 만든 유저가 존재하지 않으면 예외 발생")
    void saveManager_시_Todo를_만든_user가_null일_때__예외가_발생한다() {
        // given
        Long todoId = 1L;
        Long managerUserId = 2L;

        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        Todo todo = Todo.builder().user(null).build();
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoService.findByIdOrFail(todoId)).willReturn(todo);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertAll(
                () -> assertEquals(ErrorCode.TODO_CREATOR_NOT_FOUND.getMsg(), exception.getMessage()),
                () -> verify(todoService, times(1)).findByIdOrFail(todoId)
        );
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test
        // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }
}
