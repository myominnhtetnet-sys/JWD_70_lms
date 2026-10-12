package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.model.Review;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final JdbcTemplate jdbcTemplate;

    public ReviewService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🟢 FIXED: Added created_at column and populated it with NOW() to prevent null query drops
    public void saveReview(Review review, int userId) {
        String sql = "INSERT INTO lms_db.course_reviews (course_id, user_id, rating, comment, is_visible, created_at) " +
                     "VALUES (?, ?, ?, ?, 1, NOW())";
                     
        jdbcTemplate.update(sql, 
            review.getCourse().getCourse_id(),
            userId, 
            review.getRating(),
            review.getComment()
        );
    }

    public boolean hasUserReviewedCourse(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM lms_db.course_reviews WHERE user_id = ? AND course_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, courseId);
        return count != null && count > 0;
    }
    
    public List<Map<String, Object>> getReviewsByCourseId(int courseId) {
        // 🟢 FIXED: Used explicit lowercase column aliases to force the map keys to stay predictable
        String sql = """
                SELECT r.rating AS rating, 
                       r.comment AS comment, 
                       u.full_name AS full_name, 
                       r.created_at AS created_at
                FROM lms_db.course_reviews r
                JOIN lms_db.users u ON r.user_id = u.user_id
                WHERE r.course_id = ? AND r.is_visible = 1
                ORDER BY r.created_at DESC
                """;
        return jdbcTemplate.queryForList(sql, courseId);
    }
}
