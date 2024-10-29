package org.example.expert.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.config.auth.PasswordEncoder;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String userRole;

    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .email(this.email)
                .password(encoder.encode(this.password))
                .userRole(UserRole.of(this.userRole))
                .build();
    }
}
