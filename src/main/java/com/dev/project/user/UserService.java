package com.dev.project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::from) // 깔끔하게 변환 가능
                .collect(Collectors.toList());
    }

    public UserDTO findByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저를 찾을 수 없습니다: " + loginId));
        UserDTO userDto = UserDTO.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .roleAuth(user.getRoleAuth())
                .build();

        return userDto;
    }
}
