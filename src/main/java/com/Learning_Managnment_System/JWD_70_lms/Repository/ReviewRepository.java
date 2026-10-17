package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.Review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReviewRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Inserts a verified course review record into the persistence store.
     */
    public void save(Review review, int userId) {
        String sql = "INSERT INTO learning_db.course_reviews (course_id, user_id, rating, comment, is_visible, created_at) " +
                     "VALUES (?, ?, ?, ?, 1, NOW())";
                     
        jdbcTemplate.update(sql, 
            review.getCourse().getCourse_id(),
            userId, 
            review.getRating(),
            review.getComment()
        );
    }

    /**
     * Checks if a matching user and course pair combination row exists.
     */
    public Integer countReviewsByUserAndCourse(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM learning_db.course_reviews WHERE user_id = ? AND course_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, courseId);
    }

    /**
     * Retrieves a projected tabular array list matching specific course identifiers.
     */
    public List<Map<String, Object>> findReviewsByCourseId(int courseId) {
        String sql = """
                SELECT r.rating AS rating, 
                       r.comment AS comment, 
                       u.full_name AS full_name, 
                       r.created_at AS created_at
                FROM learning_db.course_reviews r
                JOIN learning_db.users u ON r.user_id = u.user_id
                WHERE r.course_id = ? AND r.is_visible = 1
                ORDER BY r.created_at DESC
                """;
        return jdbcTemplate.queryForList(sql, courseId);
    }
}
