package com.Learning_Managnment_System.JWD_70_lms.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.ReviewRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

	@Autowired
	 ReviewRepository reviewRepository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	/*
	 * // 🟢 Review အသစ်သိမ်းဆည်းရန် (Controller မှ ပို့ပေးသော userId ကို သုံးသည်)
	 * public void saveReview(Review review, int userId) { String sql =
	 * "INSERT INTO learning_db.course_reviews (course_id, user_id, rating, comment, is_visible, created_at) "
	 * + "VALUES (?, ?, ?, ?, 1, NOW())";
	 * 
	 * jdbcTemplate.update(sql, review.getCourse().getCourse_id(), userId,
	 * review.getRating(), review.getComment() );
	 * 
	 * }
	 */
    

    public void saveReview(Review review, int userId) {
        // Business Rule: Ensure validation steps can safely execute here before calling DB persistence bounds
        reviewRepository.save(review, userId);
    }

    // 🟢 အဆိုပါ User ဒီ Course ကို Review ပေးပြီးပြီလား စစ်ဆေးရန်
    public boolean hasUserReviewedCourse(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM learning_db.course_reviews WHERE user_id = ? AND course_id = ?";
		/*
		 * Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId,
		 * courseId);
		 */
        Integer count = reviewRepository.countReviewsByUserAndCourse(userId, courseId);
        return count != null && count > 0;
    }
    
    // 🟢 Course ID အလိုက် Review များနှင့် User နာမည်များကို Join ၍ ယူရန်
    public List<Map<String, Object>> getReviewsByCourseId(int courseId) {
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
		/* return jdbcTemplate.queryForList(sql, courseId); */
        return reviewRepository.findReviewsByCourseId(courseId);
    }
}