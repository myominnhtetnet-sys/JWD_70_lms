package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@GetMapping("/")
    public String homePage() {
        return "index"; 
    }
	@GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
           
            session.invalidate();
        }
        
        return "redirect:/"; 
    }
}
