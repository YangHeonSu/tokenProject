package com.dev.project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [함수] doFilterInternal: 모든 HTTP 요청마다 실행되는 필터 로직
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더(Authorization)에서 토큰만 쏙 뽑아옴
        String token = resolveToken(request);
        // token이 존재하고 유효성이 검증된 토큰일 경우
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 유저 정보를 꺼내 인증 객체를 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // Spring Security 전용 저장소(SecurityContext)에 인증 객체 저장
            // 위 방식으로 저장할 경우 요청이 끝날 때까지 "인증된 사용자" 유지
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * [함수] resolveToken: 헤더의 "Bearer <token>" 형태에서 토큰 문자열만 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 헤더 값이 있고 "Bearer "로 시작한다면 그 뒤의 토큰값만 잘라서 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
