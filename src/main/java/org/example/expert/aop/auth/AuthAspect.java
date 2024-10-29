package org.example.expert.aop.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.user.UserRole;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.example.expert.ex.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(requireAuthenticatedUser)")
    public Object authenticatedUser(ProceedingJoinPoint proceedingJoinPoint, RequireAuthenticatedUser requireAuthenticatedUser) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServerException(ErrorCode.CONTEXT_NOT_FOUND);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        AuthUser authUser = extractAuthInfo(request);

        validateAuthUser(authUser);
        validateUserRole(authUser, requireAuthenticatedUser);
        return proceedingJoinPoint.proceed();
    }

    private void validateUserRole(AuthUser authUser, RequireAuthenticatedUser requireAuthenticatedUser) {
        UserRole requireRole = requireAuthenticatedUser.requireRole();
        if (requireRole != UserRole.USER && !requireRole.equals(authUser.getUserRole())) {
            log.warn("권한 부족: required = {}, actual = {}", requireRole, authUser.getUserRole());
            throw new InvalidRequestException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private AuthUser extractAuthInfo(HttpServletRequest request) {
        return AuthUser.builder()
                .id((Long) request.getAttribute("userId"))
                .email((String) request.getAttribute("email"))
                .userRole(UserRole.of((String) request.getAttribute("userRole")))
                .build();
    }

    private void validateAuthUser(AuthUser authInfo) {
        if (authInfo.getId() == null) {
            log.error("인증 실패: userId is null");
            throw new ServerException(ErrorCode.INVALID_AUTH_INFO);
        }
        if (authInfo.getEmail() == null) {
            log.error("인증 실패: email is null");
            throw new ServerException(ErrorCode.INVALID_AUTH_INFO);
        }
        if (authInfo.getUserRole() == null) {
            log.error("인증 실패: userRole is null");
            throw new ServerException(ErrorCode.INVALID_AUTH_INFO);
        }
    }

}
