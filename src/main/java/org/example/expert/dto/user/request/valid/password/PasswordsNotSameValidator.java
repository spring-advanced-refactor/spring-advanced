package org.example.expert.dto.user.request.valid.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.expert.dto.user.request.UserChangePasswordRequest;

public class PasswordsNotSameValidator implements ConstraintValidator<PasswordsNotSame, UserChangePasswordRequest> {
    @Override
    public boolean isValid(UserChangePasswordRequest request, ConstraintValidatorContext context) {
        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            return true;
        }
        return !request.getOldPassword().equals(request.getNewPassword());
    }
}