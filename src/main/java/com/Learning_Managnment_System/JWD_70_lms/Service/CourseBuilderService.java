package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseBuilderRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBuilderBean;

@Service
public class CourseBuilderService {
	
	@Autowired
	private CourseBuilderRepository courseRepository;

    public List<CourseBuilderBean> getAllCourses() {
        return courseRepository.findAllCourses();
    }

    public CourseBuilderBean getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public void createCourse(CourseBuilderBean course, int currentUserId) {
        courseRepository.save(course, currentUserId);
    }

    public void updateCourse(Long courseId, CourseBuilderBean course) {
        courseRepository.update(courseId, course);
    }

    public void deleteCourse(Long courseId) {
        courseRepository.delete(courseId);
    }
}