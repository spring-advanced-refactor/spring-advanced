package org.example.expert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.service.user.UserService;
import org.example.expert.dto.user.request.UserChangePasswordRequest;
import org.example.expert.dto.user.response.UserResponse;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResult<UserResponse>> getUser(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(ApiResult.success(userService.getUser(userId)));
    }

    @PutMapping("/users")
    public ResponseEntity<ApiResult<String>> changePassword(@Auth AuthUser authUser, @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
        return ResponseEntity.ok(ApiResult.success("Password has been successfully changed"));
    }
}
