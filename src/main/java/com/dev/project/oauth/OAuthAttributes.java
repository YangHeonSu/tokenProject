package com.dev.project.oauth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private String loginId;
    private String name;
    private String email;
    private Map<String, Object> attributes;

    public static OAuthAttributes of(String registerationId, Map<String, Object> attributes) {

        if ("naver".equals(registerationId)) {
            return ofNaver(attributes);
        } else {
            return ofKakao(attributes);
        }
    }

    public static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder().loginId("naver_" + response.get("id"))
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .build();
    }

    public static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return OAuthAttributes.builder()
                .loginId("kakao_" + attributes.get("id"))
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .attributes(attributes)
                .build();
    }
}
