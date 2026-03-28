package com.dev.project.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final UserDTO userDTO;
    public CustomUserDetails(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    /**
     * [함수] getAuthorities: 유저가 가진 권한 목록을 반환
     * Security는 'ROLE_USER', 'ROLE_ADMIN' 같은 문자열을
     * SimpleGrantedAuthority 객체로 감싸서 관리합니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // DB에 저장된 권한(예: "USER")을 "ROLE_USER" 형태로 변환하여 반환
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userDTO.getRoleAuth()));
    }

    /**
     * [함수] getPassword: DB에서 조회한 암호화된 비밀번호를 Security에 전달
     */
    @Override
    public String getPassword() {
        return userDTO.getPassword();
    }

    /**
     * [함수] getUsername: 유저를 식별할 수 있는 아이디를 전달
     */
    @Override
    public String getUsername() {
        return userDTO.getLoginId();
    }

    /**
     * [함수] 계정 만료 여부: true는 만료되지 않음을 의미
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * [함수] 계정 잠김 여부: true는 잠기지 않음을 의미
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * [함수] 비밀번호 만료 여부: true는 만료되지 않음을 의미
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * [함수] 계정 활성화 여부: 휴면 계정 등이 아닐 경우 true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * [꿀팁] 필요한 경우 실제 UserVO 객체를 통째로 꺼낼 수 있도록 Getter 추가
     */
    public UserDTO getUserVO() {
        return userDTO;
    }
}
