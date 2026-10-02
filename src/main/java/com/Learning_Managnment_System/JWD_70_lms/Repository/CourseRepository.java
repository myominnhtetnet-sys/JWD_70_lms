package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ReviewMapper; // Assuming you have a ReviewMapper
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

	@Autowired
    JdbcTemplate jdbcTemplate;

    public Optional<CourseBean> findBySlug(String slug) {
        String sql = "SELECT * FROM lms_db.courses WHERE slug = ? AND deleted_at IS NULL";
        try {
            CourseBean course = jdbcTemplate.queryForObject(sql, new CourseMapper(), slug);
            return Optional.ofNullable(course);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); 
        }
    }

    public List<Review> findReviewsByCourseId(int courseId) {
        String sql = "SELECT * FROM lms_db.course_reviews WHERE course_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReviewMapper(), courseId);
    }
}
