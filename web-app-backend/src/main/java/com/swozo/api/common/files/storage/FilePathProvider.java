package com.swozo.api.common.files.storage;


import com.swozo.api.common.files.exceptions.IllegalFilenameException;
import com.swozo.api.common.files.util.FilePathGenerator;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FilePathProvider {
    private static final String SEPARATOR = "/";
    private static final String COURSES = "courses";
    private static final String ACTIVITIES = "activities";
    private static final String ACTIVITY_MODULES = "modules";
    private static final String SERVICES = "services";
    private static final String USERS = "users";

    public FilePathGenerator publicActivityFilePath(Activity activity) {
        return withFilename(COURSES, activity.getCourse().getId(), ACTIVITIES, activity.getId());
    }

    public FilePathGenerator userActivityModuleFilePath(ActivityModule activityModule, User user) {
        return withSanitizedFilename(
                ACTIVITIES,
                activityModule.getActivity().getId(),
                ACTIVITY_MODULES,
                activityModule.getId(),
                USERS,
                user.getId()
            );
    }

    public FilePathGenerator serviceModuleFilePath(ServiceModule serviceModule) {
        return withFilename(SERVICES, serviceModule.getId());
    }

    public String getFilename(String path) {
        var lastSeparatorIdx = path.lastIndexOf(SEPARATOR);
        return path.substring(lastSeparatorIdx == -1 ? 0 : lastSeparatorIdx + 1);
    }

    private String join(Object ...pathElements) {
        return String.join(SEPARATOR, Arrays.stream(pathElements).map(Object::toString).toList());
    }

    private FilePathGenerator withFilename(Object ...partialPath) {
        return filename -> join(join(partialPath), filename);
    }

    private FilePathGenerator withSanitizedFilename(Object ...partialPath) {
        return filename -> join(join(partialPath), sanitizeWithEmptyNameHandling(filename));
    }

    private String sanitizeWithEmptyNameHandling(String filename) {
        var sanitizedName = sanitizeFilename(filename);
        if (sanitizedName.isEmpty() || sanitizedName.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return sanitizedName;
    }

    public String sanitizeFilename(String filename) {
        // TODO: this is not thoroughly tested, stolen from: https://stackoverflow.com/a/13293384
        var result = new StringBuilder();

        for (char c : filename.toCharArray()) {
            if (c=='.' || Character.isJavaIdentifierPart(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public void validateFilename(String filename) {
        // TODO: do this with some pretty regex
        if (filename.contains("/") || filename.contains("\\")) {
            throw IllegalFilenameException.of(filename, List.of("/", "\\"));
        }
    }
}
