package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<ApiResult<SignupResponse>> join(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(ApiResult.success(authService.join(signupRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<SigninResponse>> login(@Valid @RequestBody SigninRequest signinRequest) {
        return ResponseEntity.ok(ApiResult.success(authService.login(signinRequest)));
    }
}
