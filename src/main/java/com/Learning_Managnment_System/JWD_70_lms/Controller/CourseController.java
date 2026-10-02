package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import java.util.Optional;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseRepository courseRepository; 

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
        
        int pageSize = 6;
        
        // 🟢 FIXED: Using pure JDBC offset-driven filtering methods instead of Spring Data Pageable
        List<CourseBean> courseList = courseService.filterCoursesJdbc(filters, page, pageSize);
        int totalPages = courseService.getTotalPagesForFilters(filters, pageSize);
        
        model.addAttribute("list", courseList);              
        model.addAttribute("currentPage", page);                           
        model.addAttribute("totalPages", totalPages);      
        
        // Binds your active pagination link params state mapping
        model.addAttribute("courseBean", filters); 
        
        return "courses"; 
    }

    @GetMapping("/detail/{slug}")
    public String getCourseDetails(@PathVariable("slug") String slug, Model model) {
        // Safe mapping using the rewritten class Optional structure container
        Optional<CourseBean> courseOpt = courseRepository.findBySlug(slug);
        
        if (courseOpt.isEmpty()) {
            return "error/404"; 
        }
        
        CourseBean course = courseOpt.get();
        
        // Fetch corresponding user reviews using our pure JDBC repository method mapping
        List<Review> courseReviews = courseRepository.findReviewsByCourseId(course.getCourse_id());
        
        model.addAttribute("course", course);
        model.addAttribute("reviews", courseReviews); 
        
        return "course-detail"; 
    }

    @PostMapping("/{id}/review")
    public String saveCourseReview(
            @PathVariable("id") Long courseId,
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

        // 2. 🟢 FIXED: Replaced .findById() with getCourseById from your service layer
        CourseBean course = courseService.getCourseById(courseId.intValue());
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "The target course data record was not found.");
            return "redirect:/courses/show";
        }

        // 3. Build and map your concrete data POJO structure fields
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(currentUser); 
        review.setCourse(course);    

        // 4. Save review instance via raw JDBC write queries
        reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback has been published.");
        
        // 5. Auto-refresh page view layout while keeping active modal open natively
        return "redirect:/courses/show#detailModal-" + courseId;
    }
}
