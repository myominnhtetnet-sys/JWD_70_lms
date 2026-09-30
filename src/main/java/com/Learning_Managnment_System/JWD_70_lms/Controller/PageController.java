package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	@GetMapping("/")
	public String show() {		
		return "dashboard";		
	}
	
	 @GetMapping("/question-list")
	    public String questionList() {
	        return "question-list";
	    }

}
