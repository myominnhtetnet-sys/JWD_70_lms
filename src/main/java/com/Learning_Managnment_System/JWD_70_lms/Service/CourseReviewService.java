package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseReviewRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseReviewBean;

@Service
public class CourseReviewService {
	
	private final CourseReviewRepository repository;

    public CourseReviewService(CourseReviewRepository repository) {
        this.repository = repository;
    }

    // 1. Get All Course Reviews (Moderation List)
    public List<CourseReviewBean> getAllReviews() {
        String sql = """
            SELECT 
                r.review_id, 
                r.course_id, 
                c.title AS course_title,
                r.user_id, 
                u.full_name AS student_name, 
                u.email AS student_email,
                r.rating, 
                r.comment, 
                r.is_visible, 
                r.created_at, 
                r.updated_at
            FROM course_reviews r
            INNER JOIN courses c ON r.course_id = c.course_id
            INNER JOIN users u ON r.user_id = u.user_id
            ORDER BY r.created_at DESC
        """;
        return repository.queryList(sql);
    }

    // 2. Get Single Review by ID
    public CourseReviewBean getReviewById(Long reviewId) {
        String sql = """
            SELECT 
                r.review_id, 
                r.course_id, 
                c.title AS course_title,
                r.user_id, 
                u.full_name AS student_name, 
                u.email AS student_email,
                r.rating, 
                r.comment, 
                r.is_visible, 
                r.created_at, 
                r.updated_at
            FROM course_reviews r
            INNER JOIN courses c ON r.course_id = c.course_id
            INNER JOIN users u ON r.user_id = u.user_id
            WHERE r.review_id = ?
        """;
        return repository.querySingle(sql, reviewId);
    }

    // 3. Get Reviews Filtered by Course ID
    public List<CourseReviewBean> getReviewsByCourseId(Long courseId) {
        String sql = """
            SELECT 
                r.review_id, 
                r.course_id, 
                c.title AS course_title,
                r.user_id, 
                u.full_name AS student_name, 
                u.email AS student_email,
                r.rating, 
                r.comment, 
                r.is_visible, 
                r.created_at, 
                r.updated_at
            FROM course_reviews r
            INNER JOIN courses c ON r.course_id = c.course_id
            INNER JOIN users u ON r.user_id = u.user_id
            WHERE r.course_id = ?
            ORDER BY r.created_at DESC
        """;
        return repository.queryList(sql, courseId);
    }

    // 4. Toggle Review Visibility (Approve / Hide)
    public boolean toggleVisibility(Long reviewId, boolean isVisible) {
        String sql = """
            UPDATE course_reviews 
            SET is_visible = ?, updated_at = NOW() 
            WHERE review_id = ?
        """;
        return repository.update(sql, isVisible ? 1 : 0, reviewId) > 0;
    }

    // 5. Delete Review
    public boolean deleteReview(Long reviewId) {
        String sql = "DELETE FROM course_reviews WHERE review_id = ?";
        return repository.update(sql, reviewId) > 0;
    }

}
