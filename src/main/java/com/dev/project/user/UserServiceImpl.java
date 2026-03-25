package com.dev.project.user;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserMapper userMapper;

    @Override
    public UserVo selectUserByUserId(String userId) {
        return userMapper.selectUserByUserId(userId);
    }
}
