package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.StudentService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    

	@GetMapping("/home")
	public String show() {		
		return "student";		
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

    @GetMapping("/student/profile")
    public String showProfilePage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student != null) {
            model.addAttribute("fullName", student.get("full_name"));
            model.addAttribute("email", student.get("email"));
            model.addAttribute("phone", student.get("phone"));
            model.addAttribute("address", student.get("address"));
            model.addAttribute("studentId", student.get("id")); 
        }

        return "profile";
    }

    @PostMapping("/student/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "address", required = false) String address,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        int result = studentRepository.updateProfile(email, fullName, phone, address);

        if (result > 0) {
            session.setAttribute("fullName", fullName); 
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile.");
        }

        return "redirect:/student/profile";
    }

    @PostMapping("/student/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and Confirm password do not match!");
            return "redirect:/student/profile";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student != null) {
            String dbPasswordHash = (String) student.get("password_hash");

            if (!dbPasswordHash.equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
                return "redirect:/student/profile";
            }

            studentRepository.updatePassword(email, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        }

        return "redirect:/student/profile";
    }
}