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
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
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
    
    @Autowired
    private StudentRepository studentRepository;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    /**
     * 🟢 1. FETCH AND FILTER CATALOG PROGRAM DASHBOARDS
     */
    @GetMapping("/show")
    public String showAndFilterCourses(
            @ModelAttribute CourseBean filters, 
            @RequestParam(value = "batchStatus", required = false) String batchStatus, 
            @RequestParam(value = "page", defaultValue = "0") int page, 
            Model model) {
        
        int pageSize = 6;
        List<CourseBean> courseList = courseService.getFilteredCourses(filters, batchStatus, page, pageSize);
        int totalPages = courseService.getTotalPagesForFilters(filters, batchStatus, pageSize);
        
        Map<String, List<java.util.Map<String, Object>>> reviewsMap = new java.util.HashMap<>();
        
        for (CourseBean course : courseList) {
            List<Map<String, Object>> reviews = reviewService.getReviewsByCourseId(course.getCourse_id());
            reviewsMap.put(String.valueOf(course.getCourse_id()), reviews);
        }
        
        model.addAttribute("list", courseList);              
        model.addAttribute("currentPage", page);                           
        model.addAttribute("totalPages", totalPages);      
        model.addAttribute("courseBean", filters); 
        model.addAttribute("selectedBatchStatus", batchStatus); 
        model.addAttribute("reviewsMap", reviewsMap); 
        
        return "courses"; 
    }

    /**
     * 🟢 2. DETAILED SINGLE VIEW VIA TEXT PATH SLUGS
     */
    /**
     * 🟢 DETAILED SINGLE VIEW VIA LOOKUP SLUGS
     */
    @GetMapping("/detail/{slug}")
    public String getCourseDetails(@PathVariable("slug") String slug, Model model) {
        // Find the course in the database case-insensitively
        Optional<CourseBean> courseOpt = courseRepository.findBySlug(slug.trim().toLowerCase());
        
        if (courseOpt.isEmpty()) {
            System.out.println("⚠️ NOTICE: Course slug not resolved: " + slug);
            return "redirect:/courses/show"; 
        }
        
        CourseBean course = courseOpt.get();
        List<Map<String, Object>> courseReviews = reviewService.getReviewsByCourseId(course.getCourse_id());
        
        model.addAttribute("course", course);
        model.addAttribute("courseReviews", courseReviews);
        
        return "course-detail"; 
    }

    @PostMapping("/{id}/review")
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

        if (reviewService.hasUserReviewedCourse(realMasterUserId, courseId.intValue())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already submitted a review for this course.");
            CourseBean course = courseService.getCourseById(courseId.intValue());
            return "redirect:/courses/detail/" + course.getSlug();
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

        reviewService.saveReview(review, realMasterUserId);

        redirectAttributes.addFlashAttribute("reviewSubmitted", true); 
        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback has been published.");

        return "redirect:/courses/show#detailModal-" + courseId;
    }
    
    
    @PostMapping("/enroll") // Maps directly to POST /courses/enroll
    public String enrollInCourse(
            @RequestParam("courseId") int courseId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Your session has expired. Please log in to enroll.");
            return "redirect:/login";
        }

        // Resolves user_id using your repository model maps safely on the fly
        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student == null || student.get("user_id") == null) {
            redirectAttributes.addFlashAttribute("error", "Student profile not found. Please log in again.");
            return "redirect:/login";
        }

        int userId = Integer.parseInt(student.get("user_id").toString());
        
        session.setAttribute("studentId", userId);
        session.setAttribute("userId", userId);

        // Invoke fully decoupled service execution pipeline
        CourseService.EnrollmentResult result = 
        		courseService.enrollStudentInCourse(userId, courseId);

        if ("SUCCESS".equals(result.getStatus())) {
            redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
            return "redirect:/student/checkout?courseId=" + result.getTargetBatchId();
        } 
        
        if ("DUPLICATE".equals(result.getStatus())) {
            redirectAttributes.addFlashAttribute("error", result.getMessage());
            return "redirect:/student/checkout?courseId=" + result.getTargetBatchId();
        } 

        redirectAttributes.addFlashAttribute("error", result.getMessage());
        return "redirect:/courses/show";
    }
}
