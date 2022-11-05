package com.swozo.api.web.activitymodule;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ActivityModuleRepository activityModuleRepository;

    public void addActivityLinks(Long scheduleRequestId, Long serviceModuleId, List<ActivityLinkInfo> links) {
        var activityModule = activityModuleRepository.findByServiceModuleId(serviceModuleId).orElseThrow();
        var scheduleInfos = activityModule.getSchedules().stream()
                .filter(scheduleInfo -> scheduleInfo.getScheduleRequestId().equals(scheduleRequestId))
                .toList();

        addLinksToActivityModule(scheduleInfos, links);
        activityModuleRepository.save(activityModule);
    }

    private void addLinksToActivityModule(List<ActivityModuleScheduleInfo> scheduleInfos, List<ActivityLinkInfo> links) {
        var userIdToLinksMap = scheduleInfos.stream()
                .flatMap(scheduleInfo -> scheduleInfo.getUserActivityLinks().stream())
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
