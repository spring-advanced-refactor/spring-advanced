package org.example.expert.aop.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(requireAuthenticatedUser)")
    public Object authenticatedUser(ProceedingJoinPoint proceedingJoinPoint, RequireAuthenticatedUser requireAuthenticatedUser) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        AuthUser authUser = null;

        for (int i = 0; i < args.length; i++) {
            if (parameterTypes[i].equals(AuthUser.class)) {
                authUser = (AuthUser) args[i];
                break;
            }
        }
        if (authUser == null) {
            log.error("인증 실패: AuthUser parameter not found");
            throw new ServerException(ErrorCode.INVALID_AUTH_INFO);
        }
        //authAuser 내부 필드 검사
        validateAuthUser(authUser);

        return proceedingJoinPoint.proceed();
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
