package com.dev.project.security;

import com.dev.project.user.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("authentication : {}", authentication);
        // 2. JWT 토큰 만들기
        // 🚨 주의: jwtTokenProvider.createToken(...) 의 파라미터는 개발자님이 만들어두신 메서드에 맞게 수정하세요!
        // 보통은 로그인 아이디(loginId)와 권한(role)을 넣어서 만듭니다.
        TokenVo tokenVo = jwtTokenProvider.generateToken(authentication);

        // 3. 토큰을 들고 프론트엔드로 튕겨내기 (리다이렉트)
        // URL 뒤에 쿼리 파라미터(?token=...)로 토큰을 달아서 보냅니다.
        // 나중에 React/Vue를 쓰시면 http://localhost:3000/oauth2/redirect?token=... 이렇게 프론트 주소로 쏘시면 됩니다!
        log.info("accessToken : {}", tokenVo.getAccessToken());
        log.info("refreshToken : {}", tokenVo.getRefreshToken());

        // 3. 두 토큰을 모두 쿼리 파라미터로 전달
        String targetUrl = UriComponentsBuilder.fromUriString("/")
                .queryParam("accessToken", tokenVo.getAccessToken())
                .queryParam("refreshToken", tokenVo.getRefreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
