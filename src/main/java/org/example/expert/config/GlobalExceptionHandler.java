package org.example.expert.config;

import org.example.expert.ex.AuthException;
import org.example.expert.ex.InvalidRequestException;
import org.example.expert.ex.ServerException;
import org.example.expert.util.api.ApiError;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> handleGeneralException(Exception e) {
        return new ResponseEntity<>(ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> validationException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        e.getBindingResult().getGlobalErrors().forEach(error ->
                errorMap.put("global", error.getDefaultMessage())
        );

        return new ResponseEntity<>(ApiResult.error(HttpStatus.BAD_REQUEST.value(), "유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResult<ApiError>> handleInvalidRequestException(InvalidRequestException ex) {
        return new ResponseEntity<>(ApiResult.error(ex.getErrorCode().getStatus(), ex.getErrorCode().getMsg()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        return getErrorResponse(status, ex.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ApiResult<ApiError>> handleServerException(ServerException ex) {
        return new ResponseEntity<>(ApiResult.error(ex.getErrorCode().getStatus(), ex.getErrorCode().getMsg()), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }
}

