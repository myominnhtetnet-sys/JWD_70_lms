package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Learning_Managnment_System.JWD_70_lms.Service.StudentService;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Controller
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

   
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new StudentBean());
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String registerStudent(@ModelAttribute("student") StudentBean student) {
        studentService.registerStudent(student);
        return "redirect:/register?success";
    }
}
