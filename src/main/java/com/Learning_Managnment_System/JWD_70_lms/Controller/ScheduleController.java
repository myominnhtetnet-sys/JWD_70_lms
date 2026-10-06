package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScheduleRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/student/schedule")
    public String showSchedulePage(HttpSession session, Model model) {
       
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Object studentIdObj = session.getAttribute("studentId");
        int userId = (studentIdObj != null) ? Integer.parseInt(studentIdObj.toString()) : 1;

        List<Map<String, Object>> batches = scheduleRepository.getStudentBatches(userId);
        List<Map<String, Object>> classSessions = scheduleRepository.getClassSessions(userId);
        List<Map<String, Object>> exams = scheduleRepository.getStudentExams(userId);

        model.addAttribute("batches", batches);
        model.addAttribute("classSessions", classSessions);
        model.addAttribute("exams", exams);
        model.addAttribute("fullName", session.getAttribute("fullName"));

        return "schedule"; 
    }
}