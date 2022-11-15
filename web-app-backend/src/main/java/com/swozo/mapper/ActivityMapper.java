package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.dto.ActivitySummaryDto;
import com.swozo.api.web.activity.dto.SelectedServiceModuleDto;
import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.activitymodule.dto.ActivityModuleDetailsDto;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static com.swozo.util.CollectionUtils.iterateSimultaneously;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
    @Autowired
    protected ServiceModuleRepository moduleRepository;
    @Autowired
    protected ActivityModuleMapper activityModuleMapper;
    @Autowired
    protected FileMapper fileMapper;
    @Autowired
    protected CommonMappers commonMappers;
    @Autowired
    protected UserMapper userMapper;

    protected LinkedList<ActivityModule> modulesToPersistence(CreateActivityRequest createActivityRequest) {
        var mappedActivityModules = new LinkedList<ActivityModule>();
        var selectedModuleIds = createActivityRequest.selectedModules().stream()
                .map(SelectedServiceModuleDto::serviceModuleId)
                .toList();

        iterateSimultaneously(
                moduleRepository.findAllById(selectedModuleIds),
                createActivityRequest.selectedModules(),
                (serviceModule, selectedServiceModuleDto) -> mappedActivityModules.push(
                        activityModuleMapper.fromServiceModule(serviceModule, selectedServiceModuleDto.linkConfirmationRequired())
                )
        );

        return mappedActivityModules;
    }

    protected List<ActivityModuleDetailsDto> modulesToDto(Activity activity, User user) {
        return activity.getModules().stream()
                .map(activityModule -> activityModuleMapper.toDto(activityModule, user))
                .toList();
    }

    protected List<SelectedServiceModuleDto> modulesToRequest(Activity activity) {
        return activity.getModules().stream()
                .map(activityModule -> new SelectedServiceModuleDto(
                        activityModule.getServiceModule().getId(),
                        activityModule.isLinkConfirmationRequired()
                ))
                .toList();
    }

    @Mapping(target = "modules", expression = "java(modulesToPersistence(createActivityRequest))")
    @Mapping(target = "instructionFromTeacherHtml", expression = "java(commonMappers.instructionToPersistence(createActivityRequest.instructionFromTeacher()))")
    protected abstract Activity basicToPersistence(CreateActivityRequest createActivityRequest);

    public Activity toPersistence(CreateActivityRequest createActivityRequest) {
        var activity = basicToPersistence(createActivityRequest);
        activity.getModules().forEach(module -> module.setActivity(activity));
        return activity;
    }

    @Mapping(target = "id", source = "activity.id")
    @Mapping(target = "name", source = "activity.name")
    @Mapping(target = "instructionFromTeacher", expression = "java(commonMappers.instructionToDto(activity.getInstructionFromTeacherHtml()))")
    @Mapping(target = "activityModules", expression = "java(modulesToDto(activity, user))")
    @Mapping(target = "publicFiles", expression = "java(activity.getPublicFiles().stream().map(fileMapper::toDto).toList())")
    public abstract ActivityDetailsDto toDto(Activity activity, User user);

    @Mapping(target = "selectedModules", expression = "java(modulesToRequest(activity))")
    public abstract CreateActivityRequest toRequest(Activity activity);

    @Mapping(target = "courseName", source = "activity.course.name")
    @Mapping(target = "courseId", source = "activity.course.id")
    @Mapping(target = "teacher", expression = "java(userMapper.toDto(activity.getTeacher()))")
    public abstract ActivitySummaryDto toSummaryDto(Activity activity);
}
