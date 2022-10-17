package com.swozo.api.common.files.storage;


import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.util.FilePathGenerator;
import com.swozo.persistence.Activity;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.ServiceModule;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FilePathProvider {
    private static final String SEPARATOR = "/";
    private static final String COURSES = "courses";
    private static final String ACTIVITIES = "activities";
    private static final String SERVICES = "services";

    public FilePathGenerator publicActivityFilePath(Activity activity) {
        return withFilename(COURSES, activity.getCourse().getId(), ACTIVITIES, activity.getId());
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
        return filename -> join(join(partialPath), sanitize(filename));
    }

    private String sanitize(String filename) {
        // TODO: this is not thoroughly tested, stolen from: https://stackoverflow.com/a/13293384
        var result = new StringBuilder();

        for (char c : filename.toCharArray()) {
            if (c=='.' || Character.isJavaIdentifierPart(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public boolean isSameName(RemoteFile file, InitFileUploadRequest initFileUploadRequest) {
        return getFilename(file.getPath()).equals(sanitize(initFileUploadRequest.filename()));
    }
}
