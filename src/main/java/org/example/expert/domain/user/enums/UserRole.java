package org.example.expert.domain.user.enums;

import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;

import java.util.Arrays;

public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.INVALID_USER_ROLE));
    }
}
