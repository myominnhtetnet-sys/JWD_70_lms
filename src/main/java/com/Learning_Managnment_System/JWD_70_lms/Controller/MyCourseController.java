package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.MyCourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class MyCourseController {

    @Autowired
    private MyCourseService courseService;

    @GetMapping("/my-courses")
    public String showMyCoursesDashboard(Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        List<Map<String, Object>> enrolledCourses = courseService.getEnrolledCoursesProgressByEmail(studentEmail);

        for (Map<String, Object> course : enrolledCourses) {
            int completed = ((Number) course.get("completed_lessons")).intValue();
            int total = ((Number) course.get("total_lessons")).intValue();
            int percent = (total > 0) ? (completed * 100 / total) : 0;
            course.put("progress_percent", percent); 
        }

        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("fullName", session.getAttribute("fullName")); 
        return "my-courses";
    }

    @GetMapping("/course/{id}")
    public String showClassroomDetail(@PathVariable("id") int courseId, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        Object roleId = session.getAttribute("roleId");
        
        if (studentEmail == null || roleId == null || (int) roleId != 3) {
            return "redirect:/login";
        }

        MyCourseBean courseMeta = courseService.getCourseMetaDetails(courseId, studentEmail);
        if (courseMeta == null) {
            return "redirect:/student/my-courses";
        }
        
        List<LessonProgressBean> lessonTimeline = 
                courseService.getCourseLessonTimeline(courseId, courseMeta.getBatchCode(), studentEmail);

        model.addAttribute("course", courseMeta);
        model.addAttribute("lessons", lessonTimeline);
        
        return "student-classroom";
    }

    @GetMapping("/course/lesson/{id}")
    public String showLessonWorkspace(
            @PathVariable("id") int lessonId, 
            @RequestParam("courseId") int courseId, 
            Model model, 
            HttpSession session) {
            
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        Map<String, Object> hybridLesson = courseService.getActiveLessonDetails(lessonId, courseId, studentEmail);
        Map<String, Object> hybridCourse = courseService.getCourseWorkspaceMetadata(studentEmail, courseId);
        List<LessonProgressBean> sidebarLessons = courseService.getSidebarLessons(studentEmail, courseId);

        model.addAttribute("lesson", hybridLesson);
        model.addAttribute("course", hybridCourse);
        model.addAttribute("sidebarLessons", sidebarLessons);

        return "student-lesson-view";
    }

    @PostMapping("/course/lesson/complete")
    public String completeLessonAction(
            @RequestParam("lessonId") int lessonId, 
            @RequestParam("courseId") int courseId,
            HttpSession session) {
            
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        try {
            int verifiedBatchId = courseService.getVerifiedBatchId(studentEmail, courseId);
            courseService.markLessonAsCompleted(verifiedBatchId, lessonId, studentEmail);
            return "redirect:/student/course/lesson/" + lessonId + "?courseId=" + courseId;
        } catch (Exception e) {
            return "redirect:/student/my-courses";
        }
    }

    @PostMapping("/course/lesson/regress")
    public String regressLessonAction(
            @RequestParam("lessonId") int lessonId,
            @RequestParam("courseId") int courseId,
            HttpSession session) {
            
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";
        
        try {
            int verifiedBatchId = courseService.getVerifiedBatchId(studentEmail, courseId);
            courseService.regressLessonProgress(verifiedBatchId, lessonId, studentEmail);
            return "redirect:/student/course/lesson/" + lessonId + "?courseId=" + courseId;
        } catch (Exception e) {
            return "redirect:/student/my-courses";
        }
    }

    @PostMapping("/course/complete")
    public String completeCourseManualAction(@RequestParam("courseId") int courseId, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";
        
        boolean success = courseService.finalizeCourseManualCheckout(studentEmail, courseId);
        if (success) {
            return "redirect:/student/awards";
        }
        return "redirect:/student/my-courses";
    }
}
