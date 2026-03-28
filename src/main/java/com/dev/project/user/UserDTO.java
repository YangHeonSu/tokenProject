package com.dev.project.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String loginId;
    private String password;
    private String roleAuth;
    private String name;
    private String email;
    private String provider;
    private String providerId;

    // Entity -> DTO 변환 정적 메서드 (선택 사항이지만 추천)
    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .roleAuth(user.getRoleAuth())
                .build();
    }

}
