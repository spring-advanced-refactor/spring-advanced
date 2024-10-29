package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserService userService;

    @Transactional
    public void changeUserRole(AuthUser authUser, UserRoleChangeRequest userRoleChangeRequest) {
        User user = userService.findByIdOrFail(authUser.getId());
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
