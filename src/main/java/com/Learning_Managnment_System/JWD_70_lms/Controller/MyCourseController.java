package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.MyCourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/student")
public class MyCourseController {

    @Autowired
    private MyCourseService courseService;

    @GetMapping("/my-courses")
    public String showMyCoursesDashboard(Model model, HttpSession session) {
        // 1. Recover the secure email string token set directly by your LoginController
        String studentEmail = (String) session.getAttribute("email");
        Object roleId = session.getAttribute("roleId");
        
        // Safety verification check: If user session has expired or role isn't STUDENT (3), kick to login page
        if (studentEmail == null || roleId == null || (int) roleId != 3) {
            System.out.println("❌ SESSION SECURITY CHECK FAILED: Redirecting to login context.");
            return "redirect:/login";
        }
        
        System.out.println("✅ SESSION ACCESS GRANTED: Fetching enrolled courses for Student Email: " + studentEmail);
        
        // 2. Query the data layer using the active email variable string token
        List<MyCourseBean> enrolledCourses = courseService.getEnrolledCoursesProgressByEmail(studentEmail);
        model.addAttribute("enrolledCourses", enrolledCourses);
        
        return "my-courses";
    }
    
    /**
     * 🟢 Renders the classroom detail timeline view for a specific course.
     * URL Target: http://localhost:8080/student/course/{id}
     */
    @GetMapping("/course/{id}")
    public String showClassroomDetail(@org.springframework.web.bind.annotation.PathVariable("id") int courseId, 
                                      Model model, HttpSession session) {
        // 1. Recover secure session email token
        String studentEmail = (String) session.getAttribute("email");
        Object roleId = session.getAttribute("roleId");
        
        if (studentEmail == null || roleId == null || (int) roleId != 3) {
            return "redirect:/login";
        }

        // 2. Fetch data via service layer routines
        MyCourseBean courseMeta = courseService.getCourseMetaDetails(courseId, studentEmail);
        
        // Safety check if student isn't enrolled or course doesn't exist
        if (courseMeta == null) {
            return "redirect:/student/my-courses";
        }
        
        // Reuse the query logic to fetch individual module lessons with their DONE status
        List<com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean> lessonTimeline = 
                courseService.getCourseLessonTimeline(courseId, courseMeta.getBatchCode());

        // 3. Bind properties to the view layout model
        model.addAttribute("course", courseMeta);
        model.addAttribute("lessons", lessonTimeline);
        
        return "student-classroom";
    }

}
