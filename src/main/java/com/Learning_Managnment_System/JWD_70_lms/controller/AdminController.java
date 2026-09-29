package com.Learning_Managnment_System.JWD_70_lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
@GetMapping("/show")
	public String show() {		
		return "dashboard";		
	}
	
}
