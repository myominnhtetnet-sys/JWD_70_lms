package com.Learning_Managnment_System.JWD_70_lms.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService;
import com.Learning_Managnment_System.JWD_70_lms.Service.ReviewService;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Controller
@RequestMapping("/coursesReview")
public class CourseReviewController {

    private final ReviewService reviewService;
    private final CourseService courseService;

    public CourseReviewController(ReviewService reviewService, CourseService courseService) {
        this.reviewService = reviewService;
        this.courseService = courseService;
    }

    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable("id") int courseId,
                               @RequestParam("rating") int rating,
                               @RequestParam("comment") String comment,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        // 1. Backend Security Check: Verify user session exists
        StudentBean currentUser = (StudentBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to leave a review.");
            return "redirect:/login";
        }

        // 2. Fetch target course
        CourseBean course = courseService.getCourseById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/courses/show";
        }

        // 3. Create and populate the Review entity
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(currentUser); // Link the session user
        review.setCourse(course);    // Link the course

        // 4. Save review via Service layer
        reviewService.saveReview(review);

        // 5. Success redirect back to the page
        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your review has been submitted.");
        return "redirect:/courses/show";
    }
}
