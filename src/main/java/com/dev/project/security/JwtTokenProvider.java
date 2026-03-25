package com.dev.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    // 시간 설정 (밀리초 단위)
    private final long accessTokenTime = 30 * 60 * 1000L;    // 30분
    private final long refreshTokenTime = 7 * 24 * 60 * 60 * 1000L; // 7일

    // RedisTemplate 주입 (String 타입으로 보통 사용)
    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisTemplate<String, String> redisTemplate) {
        // 보안을 위해 secretKey는 Base64 인코딩된 문자열이어야 함
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.redisTemplate = redisTemplate;
    }

    /**
     * [함수 수정] createToken -> generateTokenDto
     * 이제 Access와 Refresh 두 개를 만들어서 객체로 반환합니다.
     */
    public TokenVo generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // 1. Access Token 생성 (유저 권한 정보 포함)
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .expiration(new Date(now + accessTokenTime))
                .signWith(key)
                .compact();

        // 2. Refresh Token 생성 (권한 정보 제외, 보안을 위해 짧게)
        String refreshToken = Jwts.builder()
                .expiration(new Date(now + refreshTokenTime))
                .signWith(key)
                .compact();

        // 3. Redis에 Refresh Token 저장 (Key: 유저ID, Value: 토큰)
        // 마지막 인자는 TTL(만료시간) 설정입니다. 토큰 만료시간과 동일하게 맞춥니다.
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                refreshToken,
                refreshTokenTime,
                TimeUnit.MILLISECONDS
        );

        log.info("Redis에 Refresh Token 저장 완료. Key: RT:{}", authentication.getName());

        return TokenVo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * [함수] getAuthentication: 토큰에 담긴 정보를 꺼내서 Spring Security 인증 객체를 만드는 함수
     * @param token: 클라이언트가 보낸 JWT 토큰
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰을 복호화(파싱)하여 내부 데이터(Claims)를 꺼냄
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 2. 클레임에서 권한 정보("auth")를 추출하여 다시 List 형태로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3. UserDetails 객체(유저 정보)를 생성 (여기서는 Spring Security 기본 User 객체 사용)
        User principal = new User(claims.getSubject(), "", authorities);

        // 4. 최종적으로 SecurityContext에 저장할 인증 토큰 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * [함수] validateToken: 토큰의 유효성(변조 여부, 만료 여부)을 검사하는 함수
     */
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱해보고 문제가 없으면 true 반환
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 위조되었거나, 만료되었거나, 잘못된 형식일 경우 에러 로그를 남기고 false 반환
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
        }
        return false;
    }
}
