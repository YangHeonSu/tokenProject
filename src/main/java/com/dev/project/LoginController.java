package com.dev.project;

import com.dev.project.security.JwtTokenProvider;
import com.dev.project.security.TokenVo;
import com.dev.project.user.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/login")
    public ResponseEntity<TokenVo> login(@RequestBody UserVo loginRequest) {
        // 1. ID/PW로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword());
        // 2. 실제 검증 (CustomUserDetailsService가 여기서 실행됨)
        // 비밀번호가 틀리면 여기서 Exception 발생 -> 401 에러
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        // 3. 인증 성공 시 JWT 토큰 세트 발급 및 Redis 저장
        TokenVo tokenVo = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(tokenVo);
    }
}
