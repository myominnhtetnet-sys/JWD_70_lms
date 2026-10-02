package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;
import com.Learning_Managnment_System.JWD_70_lms.Service.StudentService;

@Controller
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

  
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("student", new StudentBean());
        return "register"; 
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("student") StudentBean student, RedirectAttributes redirectAttributes) {

      
        studentService.registerStudent(student); 

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
}
