package com.swozo.api.common.files.storage;


import com.swozo.api.common.files.util.FilePathGenerator;
import com.swozo.persistence.Activity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FilePathProvider {
    private static final String SEPARATOR = "/";
    private static final String COURSES = "courses";
    private static final String ACTIVITIES = "activities";

    public FilePathGenerator publicActivityFilePath(Activity activity) {
        return withFilename(COURSES, activity.getCourse().getId(), ACTIVITIES, activity.getId());
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
}
