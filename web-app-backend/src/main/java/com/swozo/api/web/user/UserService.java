package com.swozo.api.web.user;

import com.swozo.api.web.user.dto.UserAdminSummaryDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetailsDto getUserInfo(Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        return userMapper.toDto(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public List<UserAdminSummaryDto> getUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(userMapper::toAdminSummaryDto)
                .toList();
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        // TODO validate
        return userRepository.save(userMapper.toPersistence(request));
    }

    @Transactional
    public void removeUsers(List<Long> userIds) {
        // TODO: remove all files
        userRepository.deleteAllById(userIds);
    }
}
