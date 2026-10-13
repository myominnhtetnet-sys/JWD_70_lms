package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.EnrollmentRepository;

@Controller
public class EnrollmentController {
	
	@Autowired
	private  EnrollmentRepository enrollmentRepository;

   

    @GetMapping("/admin/enrollment")
    public String enrollmentPage(Model model) {

        model.addAttribute(
            "enrollments",
            enrollmentRepository.findAll()
        );

        return "enrollment";
    }

}
