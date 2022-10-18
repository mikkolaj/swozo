package com.swozo.api.web.activity;

import com.swozo.api.common.files.exceptions.DuplicateFileException;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.exceptions.types.course.NotACreatorException;
import com.swozo.api.web.exceptions.types.course.NotAMemberException;
import com.swozo.persistence.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityValidator {
    private final FilePathProvider filePathProvider;

    public void validateAddActivityFileRequest(Activity activity, Long userId, InitFileUploadRequest initFileUploadRequest) {
        if (!activity.getCourse().isCreator(userId)) {
            throw new NotACreatorException("Only course creator can add public activity files");
        }
        if (activity.getPublicFiles().stream()
                .anyMatch(file -> filePathProvider.getFilename(file.getPath()).equals(initFileUploadRequest.filename()))
        ) {
            throw DuplicateFileException.withName(initFileUploadRequest.filename());
        }
    }

    public void validateDownloadPublicActivityFileRequest(
            Long userId,
            Activity activity,
            RoleDto userRole
    ) {
        var course = activity.getCourse();
        if ((userRole == RoleDto.TEACHER && !course.isCreator(userId)) ||
            (userRole == RoleDto.STUDENT && !course.isParticipant(userId))
        ) {
            throw NotAMemberException.fromId(userId, course.getId());
        }
    }
}
