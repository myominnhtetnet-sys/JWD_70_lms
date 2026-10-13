package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest; // Spring Boot 3 အတွက် jakarta.servlet သုံးရမည်

@Controller
public class HomeController {

    @GetMapping("/admin/dashboard")
    public String dashboardPage(Model model, HttpServletRequest request) {
        // လက်ရှိ URL path (e.g. /admin/dashboard) ကို HTML ဘက်သို့ ပို့ပေးသည်
        model.addAttribute("currentUri", request.getRequestURI());
        return "dashboard"; 
    }
}