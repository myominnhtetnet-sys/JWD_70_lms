package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping({"/", "/home"})
    public String homePage() {
        return "index"; 
    }
}
