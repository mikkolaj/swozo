package com.swozo.api.web.user;

import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetailsDto getUserInfo(Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        return userMapper.toDto(user);
    }
}
