package com.Learning_Managnment_System.JWD_70_lms.Service;


import java.util.List;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Service
public class CourseService {

	private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseBean> getAllCourses() {
        return courseRepository.findAll();
    }

    public CourseBean getCourseById(Integer id) {
        return courseRepository.findById(id);
    }
    public void updateCourse(CourseBean course) {
        courseRepository.update(course);
    }
}
