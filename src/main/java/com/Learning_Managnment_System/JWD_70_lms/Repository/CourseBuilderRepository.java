package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseBuilderMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBuilderBean;


@Repository
public class CourseBuilderRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


    public List<CourseBuilderBean> findAllCourses() {
        String sql = """
            SELECT c.*, cat.name as category_name 
            FROM courses c 
            LEFT JOIN categories cat ON c.category_id = cat.category_id 
            ORDER BY c.course_id DESC
        """;
        return jdbcTemplate.query(sql, new CourseBuilderMapper());
    }

    public CourseBuilderBean findById(Long courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        List<CourseBuilderBean> courses = jdbcTemplate.query(sql, new CourseBuilderMapper(), courseId);
        return courses.isEmpty() ? null : courses.get(0);
    }

    public void save(CourseBuilderBean course) {
        String sql = """
            INSERT INTO courses (category_id, title, slug, short_description, description, thumbnail, level, language, price, duration_weeks, status, allow_discount, allow_installment, allow_scholarship, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;
        jdbcTemplate.update(sql, 
            course.getCategoryId(), course.getTitle(), course.getSlug(), 
            course.getShortDescription(), course.getDescription(), course.getThumbnail(), 
            course.getLevel(), course.getLanguage(), course.getPrice(), 
            course.getDurationWeeks(), course.getStatus(), 
            course.getAllowDiscount() ? 1 : 0, 
            course.getAllowInstallment() ? 1 : 0, 
            course.getAllowScholarship() ? 1 : 0
        );
    }

    public void update(Long courseId, CourseBuilderBean course) {
        String sql = """
            UPDATE courses SET category_id = ?, title = ?, slug = ?, short_description = ?, description = ?, thumbnail = ?, level = ?, language = ?, price = ?, duration_weeks = ?, status = ?, allow_discount = ?, allow_installment = ?, allow_scholarship = ?, updated_at = NOW() 
            WHERE course_id = ?
        """;
        jdbcTemplate.update(sql, 
            course.getCategoryId(), course.getTitle(), course.getSlug(), 
            course.getShortDescription(), course.getDescription(), course.getThumbnail(), 
            course.getLevel(), course.getLanguage(), course.getPrice(), 
            course.getDurationWeeks(), course.getStatus(), 
            course.getAllowDiscount() ? 1 : 0, 
            course.getAllowInstallment() ? 1 : 0, 
            course.getAllowScholarship() ? 1 : 0, 
            courseId
        );
    }

    public void delete(Long courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        jdbcTemplate.update(sql, courseId);
    }

}
