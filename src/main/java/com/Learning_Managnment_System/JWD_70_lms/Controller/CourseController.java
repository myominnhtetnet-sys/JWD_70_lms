package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService;
import com.Learning_Managnment_System.JWD_70_lms.Service.ReviewService;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseRepository courseRepository; // Injected for optimized combined review lookups

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/show")
    public String showAndFilterCourses(
            @ModelAttribute CourseBean filters, 
            @RequestParam(value = "page", defaultValue = "0") int page, 
            Model model) {
        
        Pageable pageable = PageRequest.of(page, 6);
        Page<CourseBean> coursePage = courseService.filterCourses(filters, pageable);
        
        model.addAttribute("list", coursePage.getContent());              
        model.addAttribute("currentPage", page);                           
        model.addAttribute("totalPages", coursePage.getTotalPages());      
        
        // 🟢 CRITICAL: Binds your active pagination link params state mapping
        model.addAttribute("courseBean", filters); 
        
        return "courses"; 
    }

    @GetMapping("/detail/{slug}")
    public String getCourseDetails(@PathVariable("slug") String slug, Model model) {
        CourseBean course = courseService.findBySlug(slug); // Calls your repository finder
        
        if (course == null) {
            return "error/404"; // Fallback if slug doesn't exist
        }
        
        // Fetch corresponding user reviews using the optimized repository query
        List<Review> courseReviews = courseRepository.findReviewsByCourseId(course.getCourse_id());
        
        model.addAttribute("course", course);
        model.addAttribute("reviews", courseReviews); // Bind reviews to display them inside your template modal
        
        return "course-details-template"; 
    }

    @PostMapping("/{id}/review")
    public String saveCourseReview(
            @PathVariable("id") Long courseId,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
            
        // 1. Backend Security Check: Verify user session exists directly from Tomcat memory container
        StudentBean currentUser = (StudentBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to leave a review.");
            return "redirect:/login";
        }

        // 2. Target validation mapping check via repository layer lookup instance
        CourseBean course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "The target course data record was not found.");
            return "redirect:/courses/show";
        }

        // 3. Build and map your concrete data entity structure fields
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(currentUser); // Connects validated user reference
        review.setCourse(course);    // Connects target course record mapping

        // 4. Save entity instance through database persistence layers
        reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback has been published.");
        
        // 5. Auto-refresh page view layout while holding the current active modal tab anchor view open natively
        return "redirect:/courses/show#detailModal-" + courseId;
    }
}
