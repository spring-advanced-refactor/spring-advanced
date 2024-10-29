package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.PasswordEncoder;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public User findByIdOrFail(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getUser(long userId) {
        return new UserResponse(findByIdOrFail(userId));
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        User user = findByIdOrFail(userId);
        authService.validatePassword(user, userChangePasswordRequest.getOldPassword());
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

}
