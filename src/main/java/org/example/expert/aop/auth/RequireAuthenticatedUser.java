package org.example.expert.aop.auth;

import org.example.expert.domain.user.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthenticatedUser {
    UserRole requireRole() default UserRole.USER;
}
