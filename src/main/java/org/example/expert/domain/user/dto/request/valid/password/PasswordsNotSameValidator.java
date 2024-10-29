package org.example.expert.domain.user.dto.request.valid.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;

public class PasswordsNotSameValidator implements ConstraintValidator<PasswordsNotSame, UserChangePasswordRequest> {
    @Override
    public boolean isValid(UserChangePasswordRequest request, ConstraintValidatorContext context) {
        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            return true;
        }
        return !request.getOldPassword().equals(request.getNewPassword());
    }
}