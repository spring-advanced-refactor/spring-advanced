package org.example.expert.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.dto.auth.request.JoinRequest;
import org.example.expert.dto.auth.request.LoginRequest;
import org.example.expert.dto.auth.response.JoinResponse;
import org.example.expert.dto.auth.response.LoginResponse;
import org.example.expert.service.AuthService;
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
    private final String AUTH_HEADER = "Authorization";

    @PostMapping("/join")
    public ResponseEntity<ApiResult<JoinResponse>> join(@Valid @RequestBody JoinRequest joinRequest, HttpServletResponse response) {
        JoinResponse joinResponse = authService.join(joinRequest);
        response.setHeader(AUTH_HEADER, joinResponse.getBearerToken());
        return ResponseEntity.ok(ApiResult.success(joinResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);
        response.setHeader(AUTH_HEADER, loginResponse.getBearerToken());
        return ResponseEntity.ok(ApiResult.success(loginResponse));
    }
}
