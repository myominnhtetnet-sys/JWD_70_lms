package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Teacher_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/teacher")
public class Teacher_Controller {

    @Autowired
    private Teacher_Repository teacherRepository;

    private boolean isNotTeacher(HttpSession session) {
        Object roleId = session.getAttribute("roleId");
        return roleId == null || (int) roleId != 2;
    }

    // Teacher Dashboard route
    @GetMapping({"", "/", "/dashboard"})
    public String teacherDashboard(HttpSession session, Model model) {
        if (isNotTeacher(session)) 
        	return "redirect:/login";

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("batches", teacherRepository.getBatchesByInstructor(instructorId));
        model.addAttribute("courses", teacherRepository.getCoursesByInstructor(instructorId));

        return "teacher"; // <-- Change this from "teacher/dashboard" to "dashboard"
    }

    // 2. My Courses Page & Course Builder
    @GetMapping("/courses")
    public String myCourses(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("courses", teacherRepository.getCoursesByInstructor(instructorId));
        return "teacher/teacher_courses"; // Updated to match your new template name
    }
    
    @GetMapping("/courses/{id}/builder")
    public String courseBuilder(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        model.addAttribute("courseId", id);
        return "teacher/course_builder";
    }

    // 3. My Batches Page & Batch Detail
    @GetMapping("/batches")
    public String myBatches(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("batches", teacherRepository.getBatchesByInstructor(instructorId));
        return "teacher/batches";
    }

    @GetMapping("/batches/{id}")
    public String batchDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        model.addAttribute("batchId", id);
        return "teacher/batch_detail";
    }

    // 4. Take Attendance Page
    @GetMapping("/attendance")
    public String takeAttendance(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        return "teacher/attendance";
    }
}