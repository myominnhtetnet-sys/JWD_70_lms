package com.Learning_Managnment_System.JWD_70_lms.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.ReviewRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
	
@Autowired
 ReviewRepository reviewRepository;

    

    public void saveReview(Review review, int userId) {
        // Business Rule: Ensure validation steps can safely execute here before calling DB persistence bounds
        reviewRepository.save(review, userId);
    }

    // 🟢 အဆိုပါ User ဒီ Course ကို Review ပေးပြီးပြီလား စစ်ဆေးရန်
    public boolean hasUserReviewedCourse(int userId, int courseId) {
        Integer count = reviewRepository.countReviewsByUserAndCourse(userId, courseId);
        return count != null && count > 0;
    }
    
    // 🟢 Course ID အလိုက် Review များနှင့် User နာမည်များကို Join ၍ ယူရန်
    public List<Map<String, Object>> getReviewsByCourseId(int courseId) {
        return reviewRepository.findReviewsByCourseId(courseId);
    }
}