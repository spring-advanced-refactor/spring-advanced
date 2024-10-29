package org.example.expert.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.service.user.UserAdminService;
import org.example.expert.dto.user.request.UserRoleChangeRequest;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/admin/users")
    public ResponseEntity<ApiResult<String>> changeUserRole(@Auth AuthUser authUser, @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(authUser, userRoleChangeRequest);
        return ResponseEntity.ok(ApiResult.success("role has been successfully changed!!!!"));
    }
}
