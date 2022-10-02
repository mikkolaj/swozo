package com.swozo.api.web.course;

import com.swozo.api.exceptions.types.course.AlreadyAMemberException;
import com.swozo.api.exceptions.types.course.NotACreatorException;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.persistence.Course;
import com.swozo.persistence.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseValidator {
    private final CourseRepository courseRepository;

    public void validateNewCourse(CreateCourseRequest createCourseRequest) {

    }

    public void validateAddStudentRequest(User student, Long teacherId, Course course) {
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new NotACreatorException("Only course creator can add a student");
        }
        validateJoinCourseRequest(student, course);
    }

    public void validateJoinCourseRequest(User student, Course course) {
        if (course.getStudents().stream().anyMatch(x -> x.getUser().getId().equals(student.getId()))) {
            throw new AlreadyAMemberException(String.format(
                    "%s already belongs to the course: %s", student.getEmail(), course.getName()
            ));
        }
    }
}
