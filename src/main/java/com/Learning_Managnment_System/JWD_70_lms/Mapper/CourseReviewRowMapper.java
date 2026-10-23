package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseReviewBean;

public class CourseReviewRowMapper implements RowMapper<CourseReviewBean> {

		@Override
	    public CourseReviewBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	        CourseReviewBean review = new CourseReviewBean();
	        
	        review.setReviewId(rs.getLong("review_id"));
	        review.setCourseId(rs.getLong("course_id"));
	        review.setCourseTitle(rs.getString("course_title"));
	        review.setUserId(rs.getLong("user_id"));
	        review.setStudentName(rs.getString("student_name"));
	        review.setStudentEmail(rs.getString("student_email"));
	        review.setRating(rs.getInt("rating"));
	        review.setComment(rs.getString("comment"));
	        review.setIsVisible(rs.getBoolean("is_visible"));

	        Timestamp createdAt = rs.getTimestamp("created_at");
	        if (createdAt != null) {
	            review.setCreatedAt(createdAt.toLocalDateTime());
	        }

	        Timestamp updatedAt = rs.getTimestamp("updated_at");
	        if (updatedAt != null) {
	            review.setUpdatedAt(updatedAt.toLocalDateTime());
	        }

	        return review;
	    }

}
