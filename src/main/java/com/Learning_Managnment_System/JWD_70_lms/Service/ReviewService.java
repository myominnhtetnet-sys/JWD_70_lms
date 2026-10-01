package com.Learning_Managnment_System.JWD_70_lms.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ReviewRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
