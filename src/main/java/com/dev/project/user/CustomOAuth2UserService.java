package com.dev.project.user;

import com.dev.project.oauth.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


/***
 * OAuth 데이터를 뜯어서, DB를 조회하고, 없으면 저장(가입)시킨 뒤, 방금 만든 CustomOAuth2User에 담아서 반환하는 클래스
 * 해당 방법은 백엔드와 프론트가 분리되지 않을 때 사용 Spring Security 기본 방식
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. 소셜 서비스별로 데이터 추출 (바구니에 담기)
        // 🌟 이 extract 객체는 람다 내부에서 'effectively final'로 인식되어 안전합니다.
        final OAuthAttributes extract = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());

        // 3. DB 저장 및 업데이트 로직
        User user = saveOrUpdate(extract);

        // 4. 시큐리티 세션에 담을 객체 반환
        return new CustomOAuth2User(user, extract.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes extract) {
        return userRepository.findByLoginId(extract.getLoginId())
                .map(entity -> {
                    entity.updateProfile(extract.getEmail(), extract.getName());
                    return userRepository.save(entity);
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .loginId(extract.getLoginId())
                        .email(extract.getEmail())
                        .name(extract.getName())
                        .role("ROLE_USER")
                        .build()));
    }
}
