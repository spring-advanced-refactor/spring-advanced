package org.example.expert.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.PasswordEncoder;
import org.example.expert.config.jwt.JwtUtil;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRepository;
import org.example.expert.dto.auth.request.JoinRequest;
import org.example.expert.dto.auth.request.LoginRequest;
import org.example.expert.dto.auth.response.JoinResponse;
import org.example.expert.dto.auth.response.LoginResponse;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public JoinResponse join(JoinRequest joinRequest) {
        if (userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new InvalidRequestException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        User savedUser = userRepository.save(joinRequest.toEntity(passwordEncoder));
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole());

        return new JoinResponse(bearerToken, savedUser);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));
        validatePassword(user, loginRequest.getPassword());
        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());
        return new LoginResponse(bearerToken, user);
    }

    public void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidRequestException(ErrorCode.USER_CURRENT_PASSWORD_MISMATCH);
        }
    }
}
