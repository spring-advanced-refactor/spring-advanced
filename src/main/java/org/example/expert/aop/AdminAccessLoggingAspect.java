package org.example.expert.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.admin.AdminAccessLog;
import org.example.expert.domain.log.admin.AdminAccessLogRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.util.CustomUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final AdminAccessLogRepository adminAccessLogRepository;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patchMapping() {
    }

    @Around("postMapping() || getMapping() || putMapping() || deleteMapping() || patchMapping()")
    public Object logAdminAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        //url 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();

        //admin 경로가 아니라면 리턴
        if (!uri.contains("/admin")) {
            return joinPoint.proceed();
        }

        //로그 필수 정보 가져오기 (사용자, 접근 시각, 요청 바디, 응답 바디, 요청 uri)
        Long userId = (Long) request.getAttribute("userId");
        User user = User.fromAuthUser(new AuthUser(userId, null, null));
        LocalDateTime accessTime = LocalDateTime.now();
        String requestBody = getRequestBody(request);

        //관리자가 요청한 메서드 실행
        Object result = joinPoint.proceed();
        String responseBody = getResponseBody(result);

        //로그 저장
        AdminAccessLog adminAccessLog = AdminAccessLog.builder()
                .accessTime(accessTime)
                .user(user)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .requestUrl(uri)
                .build();
        adminAccessLogRepository.save(adminAccessLog);
        log.info("admin API 접속 - 유저 ID: {}, 시각: {}, URL: {}", userId, accessTime, uri);

        return result;
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            String body = request.getReader().lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            return StringUtils.hasText(body) ? body : null;
        } catch (IOException | IllegalStateException e) {
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
