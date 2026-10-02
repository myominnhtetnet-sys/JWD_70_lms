package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final JdbcTemplate jdbcTemplate;

    // 🟢 Inject only JdbcTemplate (No references to a JPA ReviewRepository)
    public ReviewService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🟢 Pure JDBC manual write execution mapping to your table structure
    public void saveReview(Review review) {
        String sql = "INSERT INTO lms_db.course_reviews (course_id, user_id, rating, comment, is_visible) " +
                     "VALUES (?, ?, ?, ?, 1)";
                     
        jdbcTemplate.update(sql, 
            review.getCourse().getCourse_id(),
            review.getUser().getId(),
            review.getRating(),
            review.getComment()
        );
    }
}
