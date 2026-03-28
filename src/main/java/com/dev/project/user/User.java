package com.dev.project.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "login_id")
    private String loginId;
    private String password;
    private String roleAuth;
    private String email;
    private String name;
    private String provider;
    @Column(name = "provider_id")
    private String providerId;

    @Builder
    public User(String loginId, String password, String email, String name, String role, String provider, String providerId) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.roleAuth = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 소셜 회원이 이름이나 이메일을 변경했을 때 업데이트하는 메서드
    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
