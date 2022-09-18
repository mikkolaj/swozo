package com.swozo.api.web.user;

import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetailsDto getUserInfo(Long userId) {
        return userMapper.toDto(userRepository.getById(userId));
    }
}
