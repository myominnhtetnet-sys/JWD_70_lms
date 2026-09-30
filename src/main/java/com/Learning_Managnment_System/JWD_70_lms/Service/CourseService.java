package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.courseSpecification.CourseSpecification;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final JdbcTemplate jdbcTemplate;

    // Use constructor injection instead of field @Autowired (best practice)
    public CourseService(CourseRepository courseRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Your original plain SQL query using JdbcTemplate
    public List<CourseBean> showAllCourses() {
        String sql = "SELECT * FROM lms_db.courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'";
        return jdbcTemplate.query(sql, new CourseMapper());
    }

    // The new dynamic filtering method utilizing your Specification
    public List<CourseBean> filterCourses(CourseBean filter) {
        Specification<CourseBean> spec = CourseSpecification.filterBy(filter);
        return courseRepository.findAll(spec);
    }
 // Inside CourseService.java & CourseServiceImpl.java
    public Page<CourseBean> filterCourses(CourseBean filter, Pageable pageable) {
    	 Specification<CourseBean> spec = CourseSpecification.filterBy(filter);
    	    
    	    // DIRECT RETURN
    	    return courseRepository.findAll(spec, pageable);
    }
    
    public CourseBean getCourseById(Integer id) {
		String sql = "SELECT * FROM lms_db.courses where course_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new CourseMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null; 
		}
	}

}
