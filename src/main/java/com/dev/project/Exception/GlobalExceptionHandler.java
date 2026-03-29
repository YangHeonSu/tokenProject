package com.dev.project.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. 아이디/비밀번호가 틀렸을 때 (Security 내부에서 던짐)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        log.error("로그인 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    // 2. 유저를 찾을 수 없을 때
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, e.getMessage()));
    }

    // 3. 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("서버 에러 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "서버 내부 오류가 발생했습니다."));
    }


    // GlobalExceptionHandler 클래스 내부에 추가
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(NoResourceFoundException e) {
        if (e.getResourcePath().contains("favicon.ico")) {
            // 파비콘 요청으로 인한 에러는 무시하고 넘김
            return ResponseEntity.notFound().build();
        }
        // 다른 리소스를 못 찾은 진짜 에러의 경우 기존 로직 처리
        log.error("서버 에러 발생: ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }


}
