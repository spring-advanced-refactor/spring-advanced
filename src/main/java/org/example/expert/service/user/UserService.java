package org.example.expert.service.user;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.PasswordEncoder;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRepository;
import org.example.expert.dto.user.request.UserChangePasswordRequest;
import org.example.expert.dto.user.response.UserResponse;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.example.expert.service.AuthService;
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

    public UserResponse getUser(Long userId) {
        return new UserResponse(findByIdOrFail(userId));
    }

    @Transactional
    public void changePassword(Long userId, UserChangePasswordRequest userChangePasswordRequest) {
        User user = findByIdOrFail(userId);
        authService.validatePassword(user, userChangePasswordRequest.getOldPassword());
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

}
