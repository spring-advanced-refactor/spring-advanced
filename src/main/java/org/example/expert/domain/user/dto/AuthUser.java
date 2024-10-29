package org.example.expert.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.expert.domain.user.UserRole;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final UserRole userRole;

    @Builder
    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}
