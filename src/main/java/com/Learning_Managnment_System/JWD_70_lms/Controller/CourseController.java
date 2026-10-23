package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService;
import com.Learning_Managnment_System.JWD_70_lms.Service.ReviewService;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.CategoryService;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseFileUploadService;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final CourseFileUploadService fileUploadService;
    private final CourseService courseService;
    private final ReviewService reviewService;

    public CourseController(CourseRepository courseRepository, 
                            CategoryService categoryService, 
                            CourseFileUploadService fileUploadService,
                            CourseService courseService,
                            ReviewService reviewService) {
        this.courseRepository = courseRepository;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
        this.courseService = courseService;
        this.reviewService = reviewService;
    }

    @GetMapping({"", "/"})
    public String adminCoursesRoot() {
        return "Course_Management";
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
        
        Map<String, List<Map<String, Object>>> reviewsMap = new java.util.HashMap<>();
        
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
        
        return "Course_Management"; 
    }

    // ==========================================
    // 🟢 AJAX REST API ENDPOINTS FOR FRONTEND JS
    // ==========================================

    @GetMapping("/api/list")
    @ResponseBody
    public List<CourseBean> apiGetCoursesList() {
        return courseService.getAllCourses();
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public List<CategoryBean> apiGetCategories() {
        return categoryService.getAllCategories();
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public CourseBean apiGetCourseById(@PathVariable("id") int id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> apiCreateCourse(
            @RequestPart("course") CourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session) { // 🟢 HttpSession ထည့်သွင်းထားပါသည်
        try {
            // 1️⃣ Session ရှိမရှိနှင့် User ဝင်ထားခြင်းရှိမရှိ တင်းကျပ်စွာ စစ်ဆေးခြင်း
            LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
            
            if (currentLogin == null || currentLogin.getUser_id() <= 0) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Unauthorized: Please log in to create a course."));
            }

            // 2️⃣ created_by ကို Session ထဲမှ ID ဖြင့် အတင်းအကျပ် သတ်မှတ်ခြင်း (Null လုံးဝမဖြစ်စေရ)
            course.setCreated_by(currentLogin.getUser_id());

            // 3️⃣ ပုံ Upload စစ်ဆေးခြင်း
            if (image != null && !image.isEmpty()) {
                String fileName = fileUploadService.saveFile(image);
                course.setThumbnail(fileName);
            }

            // 4️⃣ Database သို့ သိမ်းဆည်းခြင်း
            courseService.saveCourse(course);
            return ResponseEntity.ok(Map.of("message", "Course created successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/api/update")
    @ResponseBody
    public ResponseEntity<?> apiUpdateCourse(
            @RequestPart("course") CourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                String fileName = fileUploadService.saveFile(image);
                course.setThumbnail(fileName);
            }
            courseService.updateCourse(course);
            return ResponseEntity.ok(Map.of("message", "Course updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> apiDeleteCourse(@PathVariable("id") int id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================

    @GetMapping("/detail/{slug}")
    public String getCourseDetails(@PathVariable("slug") String slug, Model model) {
        Optional<CourseBean> courseOpt = courseRepository.findBySlug(slug);
        
        if (courseOpt.isEmpty()) {
            return "error/404"; 
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
            return "redirect:/admin/courses/detail/" + course.getSlug(); 
        }

        CourseBean course = courseService.getCourseById(courseId.intValue());
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course record not found.");
            return "redirect:/admin/courses/show";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully.");
        return "redirect:/admin/courses/show";
    }
}