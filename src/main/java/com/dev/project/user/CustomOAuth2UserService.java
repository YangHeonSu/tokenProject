package com.dev.project.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
// 네이버에서 넘어온 데이터를 뜯어서, DB를 조회하고, 없으면 저장(가입)시킨 뒤, 방금 만든 CustomOAuth2User에 담아서 반환하는 클래스
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 네이버로부터 원본 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 네이버는 "response"라는 키 안에 실제 정보(id, email, name)를 담아서 줍니다.
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            throw new OAuth2AuthenticationException("네이버 응답 데이터가 없습니다.");
        }

        // 3. 🌟 핵심: 네이버의 고유 식별값(id)을 우리 서비스의 loginId로 사용합니다.
        // 네이버 id는 대략 "naver_5gBoDkb..." 이런 식으로 만들어 저장하는 게 관리에 좋습니다.
        String provider = userRequest.getClientRegistration().getRegistrationId(); // "naver"
        String providerId = (String) response.get("id");
        String loginId = provider + "_" + providerId; // ⬅️ 이게 DB의 login_id 컬럼에 들어갈 값!

        String email = (String) response.get("email");
        String name = (String) response.get("name");

        log.info("네이버 로그인 시도 - loginId: {}, name: {}", loginId, name);

        // 4. DB에 있으면 업데이트, 없으면 신규 저장 (Social Login의 정석)
        User user = userRepository.findByLoginId(loginId)
                .map(entity -> {
                    // 이미 있는 회원이면 정보 업데이트 (선택 사항)
                    entity.updateProfile(email, name);
                    return userRepository.save(entity);
                })
                .orElseGet(() -> {
                    // 신규 회원이면 새로 생성
                    User newUser = User.builder()
                            .loginId(loginId)      // 🌟 여기에 값이 들어가야 DB에 저장됩니다!
                            .name(name)
                            .email(email)
                            .provider(provider)
                            .providerId(providerId)
                            .role("ROLE_USER") // 기본 권한
                            .build();
                    return userRepository.save(newUser);
                });

        // 5. 시큐리티 세션에 담을 커스텀 유저 객체 반환
        return new CustomOAuth2User(user, attributes);
    }
}
