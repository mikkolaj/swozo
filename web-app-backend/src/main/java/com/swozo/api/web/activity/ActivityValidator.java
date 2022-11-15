package com.swozo.api.web.activity;

import com.swozo.api.common.files.exceptions.DuplicateFileException;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.exceptions.types.course.NotAMemberException;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.Activity;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityValidator {
    private final FilePathProvider filePathProvider;

    public void validateAddActivityFileRequest(Activity activity, Long userId, InitFileUploadRequest initFileUploadRequest) {
        if (activity.getPublicFiles().stream()
                .anyMatch(file -> filePathProvider.getFilename(file.getPath()).equals(initFileUploadRequest.filename()))
        ) {
            throw DuplicateFileException.withName(initFileUploadRequest.filename());
        }
    }

    public void validateDownloadPublicActivityFileRequest(
            Long userId,
            Activity activity
    ) {
        var course = activity.getCourse();
        if (!course.isParticipant(userId)) {
            throw NotAMemberException.fromId(userId, course.getId());
        }
    }

    public void validateDownloadActivityResultFileRequest(Long userId, Activity activity, RemoteFile file) {
        if (file.getOwner().getId().equals(userId) ||
            (activity.getTeacher().getId().equals(userId) && isOwnedByStudent(file, activity.getCourse()))) {
            return;
        }

        throw new UnauthorizedException("User " + userId + " is not allowed to download file " + file.getId());
    }

    public void validateIsParticipant(Long userId, Activity activity) {
        if (!activity.getCourse().isParticipant(userId)) {
            throw new UnauthorizedException("User " + userId + " doesn't participate in activity " + activity.getId());
        }
    }

    public void validateIsTeacher(Long userId, Activity activity) {
        if (!activity.getTeacher().getId().equals(userId)) {
            throw new UnauthorizedException("User " + userId + " isn't the teacher of activity " + activity.getId());
        }
    }

    private boolean isOwnedByStudent(RemoteFile file, Course course) {
        return course.getStudentsAsUsers().stream().anyMatch(user -> user.getId().equals(file.getOwner().getId()));
    }
}
