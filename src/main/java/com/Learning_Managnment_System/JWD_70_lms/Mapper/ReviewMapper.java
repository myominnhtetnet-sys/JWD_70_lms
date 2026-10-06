package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        
        review.setReviewId(rs.getLong("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // 2. 🟢 CRITICAL: Populate the nested Student POJO fields
        // This ensures review.user.fullName reads successfully on your HTML pages!
        StudentBean user = new StudentBean();
        user.setId(rs.getLong("user_id"));
        user.setFullName(rs.getString("full_name")); // Extracted via the SQL Join query execution
        
        review.setUser(user);
        
        return review;
    }
}
