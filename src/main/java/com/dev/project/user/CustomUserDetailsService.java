package com.dev.project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService; // MyBatis Mapper

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userService.findByLoginId(username);
        if (user == null) throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        return new CustomUserDetails(user); // UserDetails 구현체 반환
    }

}
