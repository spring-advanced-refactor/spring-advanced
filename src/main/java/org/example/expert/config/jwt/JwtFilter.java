package org.example.expert.config.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.UserRole;
import org.example.expert.ex.CustomJwtException;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.example.expert.util.CustomUtil;
import org.example.expert.util.api.ApiResult;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        if (url.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");

        //토큰 검증에 실패하면 오류 응답 반환하고 체인 중단
        if (!checkTokenPresence(token, httpResponse)) {
            return;
        }
        validateTokenAndSetUserInfo(request, response, token, chain, httpResponse, httpRequest);
    }

    private void validateTokenAndSetUserInfo(ServletRequest request, ServletResponse response,
                                             String token, FilterChain chain, HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws IOException, ServletException {
        String url = httpRequest.getRequestURI();
        try {
            String jwt = jwtUtil.substringToken(token);
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            //유저 정보 세팅
            setUserAttributes(httpRequest, claims);
            //관리자 uri 접속한 경우 권한 검증
            if (url.startsWith("/admin")) {
                validateAdminAccess(claims);
            }

            chain.doFilter(request, response);
        } catch (CustomJwtException e) {
            setErrorResponse(httpResponse, e.getErrorCode().getStatus(), e.getErrorCode().getMsg());
        }
    }

    private void validateAdminAccess(Claims claims) {
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));
        if (!UserRole.ADMIN.equals(userRole)) {
            throw new InvalidRequestException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private void setUserAttributes(HttpServletRequest request, Claims claims) {
        request.setAttribute("userId", Long.parseLong(claims.getSubject()));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));
    }

    private boolean checkTokenPresence(String token, HttpServletResponse response) throws IOException {
        if (token == null || token.isEmpty()) {
            setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ErrorCode.TOKEN_NOT_FOUND.getMsg());
            return false;
        }
        return true;
    }

    private void setErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {
        ApiResult<Object> unAuthResponse = ApiResult.error(status, msg);
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(unAuthResponse));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
