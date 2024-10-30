package org.example.expert.aop.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.expert.domain.log.admin.AdminAccessLog;
import org.example.expert.domain.log.admin.AdminAccessLogRepository;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.util.CustomUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final AdminAccessLogRepository adminAccessLogRepository;
    private static final String ADMIN_PATH = "/admin";

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void apiMapping() {
    }

    @Around("apiMapping()")
    public Object logAdminAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        //url 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();

        //admin 경로가 아니라면 리턴
        if (!isAdminRequest(uri)) {
            return joinPoint.proceed();
        }
        //로그 필수 정보 가져오기 (사용자, 접근 시각, 요청 바디, 응답 바디, 요청 uri)
        Long userId = getUserId(request);
        UserRole userRole = getUserRole(request);

        if (userId == null) {
            log.warn("인증 실패: userId is null");
            return joinPoint.proceed();
        }
        if (!isAuthorizedAdmin(userRole)) {
            log.warn("권한 없는 유저의 관리자 API 접근 시도: {}", userId);
            return joinPoint.proceed();
        }

        LocalDateTime accessTime = LocalDateTime.now();
        String requestBody = getRequestBody(joinPoint);
        //관리자가 요청한 메서드 실행
        Object result = joinPoint.proceed();
        //로그 저장
        saveAccessLog(userId, uri, accessTime, requestBody, result);

        return result;
    }


    private void saveAccessLog(Long userId, String uri, LocalDateTime accessTime, String requestBody, Object result) {
        try {
            User user = User.fromAuthUser(new AuthUser(userId, null, null));
            String responseBody = getResponseBody(result);
            AdminAccessLog adminAccessLog = AdminAccessLog.builder()
                    .accessTime(accessTime)
                    .user(user)
                    .requestBody(requestBody)
                    .responseBody(responseBody)
                    .requestUrl(uri)
                    .build();
            adminAccessLogRepository.save(adminAccessLog);
            log.info("admin API 접속 - 유저 ID: {}, 시각: {}, URL: {}", userId, accessTime, uri);
        } catch (Exception e) {
            log.warn("admin API 접근 기록 저장 실패 - 유저 ID: {}, 시각: {}, URL: {}", userId, accessTime, uri, e);
        }
    }

    private boolean isAuthorizedAdmin(UserRole userRole) {
        return userRole == UserRole.ADMIN;
    }

    private boolean isAdminRequest(String uri) {
        return uri != null && uri.contains(ADMIN_PATH);
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private UserRole getUserRole(HttpServletRequest request) {
        return UserRole.of((String) request.getAttribute("userRole"));
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        Map<String, Object> requestMap = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();

        try {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Object arg = args[i];
                if (parameter.getAnnotation(RequestBody.class) != null) {
                    requestMap.put("requestBody", CustomUtil.convertToJson(arg));
                }
                if (parameter.getAnnotation(ModelAttribute.class) != null) {
                    requestMap.put("modelAttribute", CustomUtil.convertToJson(arg));
                }
            }
            return CustomUtil.convertToJson(requestMap);
        } catch (JsonProcessingException e) {
            log.warn("요청 바디 본문을 읽는 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    private String getResponseBody(Object result) {
        try {
            return result != null ? CustomUtil.convertToJson(result) : null;
        } catch (JsonProcessingException e) {
            log.warn("응답 바디 본문을 읽는 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }


}
