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
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
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
            @RequestParam(value = "batchStatus", required = false) String batchStatus, 
            @RequestParam(value = "page", defaultValue = "0") int page, 
            Model model) {
        
        int pageSize = 6;
        List<CourseBean> courseList = courseService.getFilteredCourses(filters, batchStatus, page, pageSize);
        int totalPages = courseService.getTotalPagesForFilters(filters, batchStatus, pageSize);
        
        // 🟢 FIXED: Create a robust, universally queryable flat hash map instance container 
        java.util.Map<String, List<java.util.Map<String, Object>>> reviewsMap = new java.util.HashMap<>();
        
        for (CourseBean course : courseList) {
            List<java.util.Map<String, Object>> reviews = reviewService.getReviewsByCourseId(course.getCourse_id());
            // Stringify the course ID mapping key accurately to prevent lookup evaluation errors
            reviewsMap.put(String.valueOf(course.getCourse_id()), reviews);
        }
        
        model.addAttribute("list", courseList);              
        model.addAttribute("currentPage", page);                           
        model.addAttribute("totalPages", totalPages);      
        model.addAttribute("courseBean", filters); 
        model.addAttribute("selectedBatchStatus", batchStatus); 
        
        // Pass the unified database review list tracker map container down to your template engine
        model.addAttribute("reviewsMap", reviewsMap); 
        
        return "courses"; 
    }

    @GetMapping("/detail/{slug}")
    public String getCourseDetails(@PathVariable("slug") String slug, Model model) {
        Optional<CourseBean> courseOpt = courseRepository.findBySlug(slug);
        
        if (courseOpt.isEmpty()) {
            return "error/404"; 
        }
        
        CourseBean course = courseOpt.get();
        
        // 🟢 FIX: Fetch using your service's working JOIN method
        List<Map<String, Object>> courseReviews = reviewService.getReviewsByCourseId(course.getCourse_id());
        
        model.addAttribute("course", course);
        model.addAttribute("courseReviews", courseReviews); // Passed straight as a clean list
        
        return "course-detail"; 
    }
    
    // 🟢 FIXED: Added an explicit absolute context path slash to ensure it binds to /courses/{id}/review cleanly
    @PostMapping("/{id}/review") // Resolves to /courses/{id}/review natively
    public String saveCourseReview(
            @PathVariable("id") Long courseId,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
            
        LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
        if (currentLogin == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to leave a review.");
            return "redirect:/login"; 
        }

        int realMasterUserId = currentLogin.getUser_id(); 
        if (realMasterUserId == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid user session. Please log in again.");
            return "redirect:/login";
        }

        // Duplicate Review Prevention Check
        if (reviewService.hasUserReviewedCourse(realMasterUserId, courseId.intValue())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already submitted a review for this course.");
            CourseBean course = courseService.getCourseById(courseId.intValue());
            return "redirect:/courses/detail/" + course.getSlug(); // Redirect back to details page
        }

        CourseBean course = courseService.getCourseById(courseId.intValue());
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course record not found.");
            return "redirect:/courses/show";
        }

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setCourse(course);    

        StudentBean studentUser = new StudentBean();
        review.setUser(studentUser); 

        // Save row into your MySQL DB
        reviewService.saveReview(review, realMasterUserId);

     // This tells the page a new review was just successfully added
     redirectAttributes.addFlashAttribute("reviewSubmitted", true); 
     redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback has been published.");

     return "redirect:/courses/show#detailModal-" + courseId;
    }



   

}
