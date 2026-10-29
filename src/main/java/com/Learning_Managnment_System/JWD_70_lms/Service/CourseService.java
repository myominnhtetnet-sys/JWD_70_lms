
package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Get all courses
    public List<CourseBean> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get filtered courses with pagination
    public List<CourseBean> getFilteredCourses(
            CourseBean filters,
            String batchStatus,
            int page,
            int pageSize) {

        return courseRepository.findFilteredCourses(
                filters, batchStatus, page, pageSize);
    }

    // Get total pages for filtered courses
    public int getTotalPagesForFilters(
            CourseBean filters,
            String batchStatus,
            int pageSize) {

        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero.");
        }

        int totalItems = courseRepository.countFilteredCourses(
                filters, batchStatus);

        return Math.max(1, (int) Math.ceil(
                (double) totalItems / pageSize));
    }

    // Get course by ID
    public CourseBean getCourseById(int courseId) {
        return courseRepository.findById(courseId);
    }

    // Get active and published courses
    public List<Map<String, Object>> getActivePublishedCourses() {
        return courseRepository.findActivePublishedCourses();
    }

    // Save course
    public void saveCourse(CourseBean course) {
        courseRepository.save(course);
    }

    // Update course
    public void updateCourse(CourseBean course) {
        courseRepository.update(course);
    }

    // Soft delete course
    public void deleteCourse(int courseId) {
        courseRepository.softDelete(courseId);
    }
}
