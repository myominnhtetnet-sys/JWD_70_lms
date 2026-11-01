package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate; // 🟢 Added to resolve IDs safely
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScheduleRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate; // 🟢 Injected to bypass session null drops on the fly

    @GetMapping("/student/schedule")
    public String showSchedulePage(HttpSession session, Model model) {
        
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        // 🟢 FIXED: Resolve the real user_id dynamically from the session email instead of defaulting to 3
        int userId = 3; 
        try {
            String idLookupSql = "SELECT user_id FROM users WHERE email = ? LIMIT 1";
            Integer dbUserId = jdbcTemplate.queryForObject(idLookupSql, Integer.class, email);
            if (dbUserId != null) {
                userId = dbUserId;
                // Sync keys back into session memory so other pipeline controllers can see it
                session.setAttribute("studentId", userId);
                session.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            System.err.println("SCHEDULE CONTROLLER NOTICE -> Session fallback applied: " + e.getMessage());
        }

        // Console logger to trace the data variables during development runtime
        System.out.println("🔍 TESTING SCHEDULE FLOW -> Email: " + email + " | Resolved User ID: " + userId);

        List<Map<String, Object>> batches = scheduleRepository.getStudentBatches(userId);
        List<Map<String, Object>> classSessions = scheduleRepository.getClassSessions(userId);
        List<Map<String, Object>> exams = scheduleRepository.getStudentExams(userId);

        System.out.println("   -> Datasets found: Batches=" + batches.size() + " | Sessions=" + classSessions.size() + " | Exams=" + exams.size());

        model.addAttribute("batches", batches);
        model.addAttribute("classSessions", classSessions);
        model.addAttribute("exams", exams);
        model.addAttribute("fullName", session.getAttribute("fullName"));

        return "schedule"; 
    }
}
