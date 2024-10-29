package org.example.expert.dto.manager.response;

import lombok.Getter;
import org.example.expert.dto.user.response.UserResponse;

@Getter
public class ManagerResponse {

    private final Long id;
    private final UserResponse user;

    public ManagerResponse(Long id, UserResponse user) {
        this.id = id;
        this.user = user;
    }
}
