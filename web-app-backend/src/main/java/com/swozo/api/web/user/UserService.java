package com.swozo.api.web.user;

import com.swozo.api.common.files.FileRepository;
import com.swozo.api.common.files.FileService;
import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.user.dto.FavouriteFileDto;
import com.swozo.api.web.user.dto.MeDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.mapper.ActivityMapper;
import com.swozo.mapper.FileMapper;
import com.swozo.mapper.UserMapper;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserFavouriteFile;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FileRepository fileRepository;
    private final UserFavouriteFileRepository userFavouriteFileRepository;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final ActivityMapper activityMapper;
    private final ActivityRepository activityRepository;

    public MeDto getUserInfo(Long userId) {
        return toMeDto(getUserById(userId));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.of(userId));
    }

    public User createUserInternally(String name, String surname, String email, String password, List<RoleDto> roles) {
        return userRepository.save(
                new User(
                    name,
                    surname,
                    email,
                    authService.hashPassword(password),
                    userMapper.rolesToPersistence(roles)
                )
        );
    }

    @Transactional
    public MeDto setFileAsFavourite(Long userId, Long activityId, Long fileId) {
        var file = fileRepository.findById(fileId).orElseThrow();
        var activity = activityRepository.findById(activityId).orElseThrow();
        if (!file.getOwner().getId().equals(userId) &&
            activity.getCourse().getStudentsAsUsers().stream().noneMatch(user -> user.getId().equals(userId))
        ) {
            throw new UnauthorizedException("User " + userId + " can't mark file " + file + " as favourite");
        }

        var user = getUserById(userId);
        if (user.getFavouriteFiles().stream().noneMatch(favFile -> favFile.getId().getFileId().equals(fileId))) {
            user.addFavouriteFile(file, activity);
            userRepository.save(user);
        }

        return toMeDto(user);
    }

    @Transactional
    public MeDto unsetFileAsFavourite(Long userId, Long fileId) {
        var user = getUserById(userId);
        var favFile = user.getUserFavouriteFile(fileId).orElseThrow();
        user.removeFavouriteFile(favFile);
        userFavouriteFileRepository.delete(favFile);

        userRepository.save(user);
        return toMeDto(user);
    }

    public StorageAccessRequest getFavouriteFileDownloadRequest(Long userId, Long fileId) {
        var user = getUserById(userId);
        var file = user.getFavouriteFiles().stream()
                .map(UserFavouriteFile::getRemoteFile)
                .filter(favFile -> favFile.getId().equals(fileId))
                .findAny()
                .orElseThrow(() -> new UnauthorizedException("User " + userId + " can't download file " + fileId));

        return fileService.createExternalDownloadRequest(file);
    }

    public List<UserDetailsDto> getSystemAdmins() {
        var adminRole = roleRepository.findByName(RoleDto.ADMIN.toString());
        return userRepository.getUsersByRolesIn(List.of(adminRole)).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeUsers(List<Long> userIds) {
        // TODO: remove all files
        userRepository.deleteAllById(userIds);
    }

    private MeDto toMeDto(User user) {
        var favouriteFiles = user.getFavouriteFiles().stream()
                .map(favFile -> new FavouriteFileDto(
                        fileMapper.toDto(favFile.getRemoteFile()),
                        activityMapper.toSummaryDto(favFile.getActivity())
                ))
                .toList();
        return userMapper.toMeDto(user, favouriteFiles);
    }
}
