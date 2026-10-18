package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<CourseBean> getFilteredCourses(CourseBean filters, String batchStatus, int page, int pageSize) {
        return courseRepository.findFilteredCourses(filters, batchStatus, page, pageSize);
    }

    public int getTotalPagesForFilters(CourseBean filters, String batchStatus, int pageSize) {
        int totalItems = courseRepository.countFilteredCourses(filters, batchStatus);
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    public CourseBean getCourseById(int courseId) {
        return courseRepository.findById(courseId);
    }
    
    public List<Map<String, Object>> getActivePublishedCourses() {
        return courseRepository.findActivePublishedCourses();
    }
}
