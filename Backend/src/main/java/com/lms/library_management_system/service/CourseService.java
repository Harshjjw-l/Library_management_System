package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Course;
import com.lms.library_management_system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public Course findCourseById(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
    }

    public Course updateCourse(String courseId, Course courseDetails) {
        Course existingCourse = findCourseById(courseId);
        existingCourse.setName(courseDetails.getName());
        existingCourse.setCredits(courseDetails.getCredits());
        existingCourse.setDepartment(courseDetails.getDepartment());
        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(String courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }
}