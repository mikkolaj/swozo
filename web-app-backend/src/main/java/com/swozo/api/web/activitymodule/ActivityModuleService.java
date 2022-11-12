package com.swozo.api.web.activitymodule;

import com.swozo.api.common.files.FileService;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.UserMapper;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.users.ActivityRole;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ActivityModuleRepository activityModuleRepository;
    private final UserActivityModuleInfoRepository userActivityModuleInfoRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;
    private final FilePathProvider filePathProvider;

    @Transactional
    public void addActivityLinks(Long activityModuleId, Long scheduleRequestId, List<ActivityLinkInfo> links) {
        var activityModule = activityModuleRepository.findById(activityModuleId).orElseThrow();
        var scheduleInfos = activityModule.getSchedules().stream()
                .filter(scheduleInfo -> scheduleInfo.getScheduleRequestId().equals(scheduleRequestId))
                .toList();

        addLinksToActivityModule(scheduleInfos, links);
        activityModuleRepository.save(activityModule);
    }

    public List<OrchestratorUserDto> getUserDataForProvisioner(Long activityModuleId, Long scheduleRequestId) {
        var activity = activityModuleRepository.findById(activityModuleId).orElseThrow();
        var teacher = activity.getActivity().getCourse().getTeacher();

        return userRepository.getUsersThatUseVmCreatedIn(activityModuleId, scheduleRequestId).stream()
                .map(user -> userMapper.toOrchestratorDto(user, user.equals(teacher) ? ActivityRole.TEACHER : ActivityRole.STUDENT))
                .toList();
    }

    private void addLinksToActivityModule(List<ActivityModuleScheduleInfo> scheduleInfos, List<ActivityLinkInfo> links) {
        var userIdToLinksMap = scheduleInfos.stream()
                .flatMap(scheduleInfo -> scheduleInfo.getUserActivityModuleData().stream())
                .collect(Collectors.toMap(
                        link -> link.getUser().getId(),
                        Function.identity()
                ));

        links.forEach(link -> {
            Optional.ofNullable(userIdToLinksMap.get(link.userId()))
                    .ifPresentOrElse(userActivityLink -> {
                        userActivityLink.setUrl(link.url());
                        link.connectionInstructionHtml()
                                .forEach((language, value) -> userActivityLink.setTranslation(new TranslatableActivityLink(language, value)));
                    },
                    () -> logUserNotFoundForLink(scheduleInfos, link));
        });
    }

    public StorageAccessRequest prepareUserActivityFileUpload(
            InitFileUploadRequest initFileUploadRequest,
            Long activityModuleId,
            Long userId
    ) {
        var activityModule = activityModuleRepository.findById(activityModuleId).orElseThrow();
        var user = userService.getUserById(userId);

        return fileService.prepareInternalUpload(
                initFileUploadRequest,
                filePathProvider.userActivityModuleFilePath(activityModule, user)
        );
    }

    @Transactional
    public void ackUserActivityFileUpload(
            UploadAccessDto uploadAccessDto,
            Long activityModuleId,
            Long scheduleRequestId,
            Long userId
    ) {
        var user = userService.getUserById(userId);
        var userActivityModuleInfo = userActivityModuleInfoRepository
                .findUserActivityModuleInfoBy(activityModuleId, scheduleRequestId, userId).orElseThrow();
        var file = fileService.acknowledgeInternalUploadWithoutTxn(user, uploadAccessDto);
        userActivityModuleInfo.setUserFile(file);
        userActivityModuleInfoRepository.save(userActivityModuleInfo);
    }

    private void logUserNotFoundForLink(List<ActivityModuleScheduleInfo> scheduleInfos, ActivityLinkInfo activityLinkInfo) {
        logger.error("Failed to add links to user with id: {}, no such UserActivityLink in scope of ActivityModule with id: {}",
                activityLinkInfo.userId(),
                scheduleInfos.stream()
                        .findAny()
                        .map(scheduleInfo -> scheduleInfo.getActivityModule().getId())
                        .orElseThrow(() -> new IllegalStateException("Received link {} but no matching scheduleInfos were found"))
        );
    }
}
