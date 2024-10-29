package org.example.expert.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.dto.user.request.valid.password.PasswordsNotSame;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@PasswordsNotSame
public class UserChangePasswordRequest {

    @NotBlank(message = "기존 비밀번호를 입력해야 합니다")
    private String oldPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해야 합니다")
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,16}$", message = "비밀번호는 숫자와 대소문자를 포함해 최소 8자에서 최대 16자여야 합니다")
    private String newPassword;
}
