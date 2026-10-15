package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student/scholarship")
public class ScholarshipController {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String showScholarshipPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student != null) {
          
            Object idObj = student.get("user_id"); 
            int userId = 0;
            if (idObj instanceof Number) {
                userId = ((Number) idObj).intValue();
            }

          
            List<ScholarshipApplicationBean> scholarships = scholarshipRepository.getApplicationsByUserId(userId);
            
            model.addAttribute("scholarships", scholarships);
            model.addAttribute("fullName", student.get("full_name"));
        }

        return "scholarship"; 
    }
 
    @GetMapping("/details/{id}")
    public String viewScholarshipDetail(@PathVariable("id") int applicationId, HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> detail = scholarshipRepository.getApplicationDetailById(applicationId);
        model.addAttribute("detail", detail);
        model.addAttribute("userEmail", email);

        return "scholarship-detail"; 
    }
}