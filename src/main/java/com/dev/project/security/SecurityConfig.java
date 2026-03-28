package com.dev.project.security;

import com.dev.project.user.CustomOAuth2UserService;
import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    // 🌟 2. 우리가 만든 CustomOAuth2UserService 주입받기
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 1. URL 권한 설정 (로그인 페이지 경로 추가!)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                        // "/login" 경로를 누구나 볼 수 있게 추가해줍니다.
                        .requestMatchers("/api/login", "/api/join", "/api/reissue", "/login").permitAll()
                        .anyRequest().authenticated()
                )

                // (기존 예외처리 코드 동일)
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.setContentType("application/json;charset=utf-8");
//                            response.getWriter().write("{\"status\": 401, \"message\": \"토큰이 유효하지 않거나 만료되었습니다.\"}");
//                        })
//                )

// 🌟 3. OAuth2 로그인 설정에 우리가 만든 서비스 장착!
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // ⬅️ "네이버 인증 끝나면 이 클래스 실행해!" 라고 지시
                        )
                        .successHandler(oAuth2SuccessHandler) // 이건 다음 스텝(JWT 발급)에서 만들 겁니다!
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * [추가] AuthenticationManager:
     * 로그인 컨트롤러에서 실제 인증을 진행할 때(ID/PW 확인) 이 객체가 필요합니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 🌟 favicon.ico 요청은 시큐리티 필터 체인을 아예 타지 않도록 설정합니다.
        return (web) -> web.ignoring().requestMatchers("/favicon.ico");
    }

}
