package org.example.expert.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.example.expert.domain.user.User;

@Getter
public class LoginResponse {

    @JsonIgnore
    private final String bearerToken;
    private final Long userId;

    public LoginResponse(String bearerToken, User user) {
        this.bearerToken = bearerToken;
        this.userId = user.getId();
    }
}
