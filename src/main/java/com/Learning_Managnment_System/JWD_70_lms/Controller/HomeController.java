package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 🟢 FIXED: Safe Spring UI template container import
import org.springframework.web.bind.annotation.GetMapping;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService; // Make sure this matches your actual service path

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    // 🟢 DECLARE THE SERVICE INJECTION CONTEXT
    private final CourseService courseService;

    // 🟢 INJECT SERVICE VIA CONSTRUCTOR BEAN INJECTION
    public HomeController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String homePage(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/student/exam-list"; 
        }
        
        // Pass the active repository content pool array straight into the index template
        List<Map<String, Object>> liveCourses = courseService.getActivePublishedCourses();
        model.addAttribute("courses", liveCourses);
        
        return "index"; 
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout"; 
    }
}
