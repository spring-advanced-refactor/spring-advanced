package org.example.expert.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 접근입니다"),
    FORBIDDEN_ACCESS(403, "관리자 권한이 필요합니다"),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰 서명 입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰입니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST.value(), "잘못된 JWT 토큰입니다"),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 일정을 찾을 수 없습니다"),
    TODO_CREATOR_PERMISSION_DENIED(HttpStatus.FORBIDDEN.value(), "해당 일정을 만든 유저가 아닙니다"),
    TODO_CREATOR_SELF_ASSIGNMENT_INVALID(HttpStatus.BAD_REQUEST.value(), "일정 작성자는 본인을 담당자로 등록할 수 없습니다"),
    TODO_ASSIGNMENT_TARGET_USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "담당자로 등록하려고 하는 유저가 존재하지 않습니다"),
    MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 담당 기록을 찾을 수 없습니다"),
    MANAGER_NOT_IN_TODO(HttpStatus.BAD_REQUEST.value(), "해당 일정에 관한 담당 정보가 아닙니다"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "해당 유저를 찾을 수 없습니다"),
    USER_NEW_PASSWORD_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST.value(), "새 비밀번호는 기존 비밀번호와 같을 수 없습니다"),
    USER_CURRENT_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 유저 권한입니다"),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 이메일입니다");

    private final String msg;
    private final int status;

    ErrorCode(int status, String msg) {
        this.msg = msg;
        this.status = status;
    }
}
