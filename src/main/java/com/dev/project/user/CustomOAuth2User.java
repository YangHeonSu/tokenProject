package com.dev.project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//OAuth 로그인 성공 후, 스프링 시큐리티에게 넘겨줄 우리가 만든 유저 전용 인증 객체
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user; // 우리 DB에서 조회하거나 방금 저장한 그 회원 엔티티!
    private final Map<String, Object> attributes; // 네이버/카카오에서 받은 원본 데이터


    // JWT 발급 핸들러에서 이 메서드를 호출해서 회원의 loginId를 가져감.
    public String getLoginId() {
        return user.getLoginId();
    }

    public String getRole() {
        return user.getRoleAuth();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleAuth()));
        return authorities;
    }

    @Override
    public String getName() {
        return user.getName();
    }
}
