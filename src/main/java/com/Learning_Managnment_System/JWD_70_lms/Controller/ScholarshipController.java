package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student/scholarship")
public class ScholarshipController {

    @GetMapping
    public String showScholarshipPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> scholarships = new ArrayList<>(); 
        model.addAttribute("scholarships", scholarships);
        model.addAttribute("fullName", session.getAttribute("fullName"));

        return "scholarship";
    }
}