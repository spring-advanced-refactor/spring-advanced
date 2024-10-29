package org.example.expert.service.user;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.auth.RequireAuthenticatedUser;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.dto.user.request.UserRoleChangeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserService userService;

    @Transactional
    @RequireAuthenticatedUser(requireRole = UserRole.ADMIN)
    public void changeUserRole(AuthUser authUser, UserRoleChangeRequest userRoleChangeRequest) {
        User user = userService.findByIdOrFail(authUser.getId());
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
