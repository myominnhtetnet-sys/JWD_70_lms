package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseReviewService;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseReviewBean;

@Controller
@RequestMapping("/admin/reviews")
public class CourseReviewController {

    private final CourseReviewService reviewService;

    public CourseReviewController(CourseReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 1. Review Moderation Page (List All Reviews or Filter by Course)
    // URL: GET /admin/reviews
    @GetMapping
    public String showReviewModerationPage(@RequestParam(name = "courseId", required = false) Long courseId, Model model) {
        List<CourseReviewBean> reviewList;

        if (courseId != null) {
            // Course ID အလိုက် Filter လုပ်ပြီးဆွဲမည်
            reviewList = reviewService.getReviewsByCourseId(courseId);
        } else {
            // Review အားလုံးကို ဆွဲမည်
            reviewList = reviewService.getAllReviews();
        }

        model.addAttribute("reviews", reviewList);
        model.addAttribute("selectedCourseId", courseId);
        
        return "Course_Review_Moderation"; // သက်ဆိုင်ရာ HTML template နာမည် ပြန်ပေးမည်
    }

    // 2. JS / Fetch API အတွက် JSON Return ပြန်ပေးမည့် Endpoint
    // URL: GET /admin/reviews/api/list
    @GetMapping("/api/list")
    @ResponseBody
    public List<CourseReviewBean> getReviewsApi() {
        return reviewService.getAllReviews();
    }

    // 3. Toggle Visibility (Show / Hide Review)
    // URL: POST /admin/reviews/toggle-visibility
    @PostMapping("/toggle-visibility")
    public String toggleReviewVisibility(
            @RequestParam("reviewId") Long reviewId,
            @RequestParam("isVisible") boolean isVisible,
            RedirectAttributes redirectAttributes) {

        boolean success = reviewService.toggleVisibility(reviewId, isVisible);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Review status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update review status.");
        }

        return "redirect:/admin/reviews";
    }

    // 4. Delete Review
    // URL: POST /admin/reviews/delete
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam("reviewId") Long reviewId,
            RedirectAttributes redirectAttributes) {

        boolean success = reviewService.deleteReview(reviewId);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Review deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete review.");
        }

        return "redirect:/admin/reviews";
    }
}