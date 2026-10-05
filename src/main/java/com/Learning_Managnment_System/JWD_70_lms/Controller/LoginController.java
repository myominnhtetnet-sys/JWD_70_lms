package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.LoginRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	@Autowired
	private LoginRepository loginRepo;

//	@GetMapping("/login")
//	public String showLogin(Model model) {
//		model.addAttribute("login", new LoginBean());
//		return "login";
//	}
	
	
	@GetMapping("/login")
	public String loginPage(
	        @RequestParam(
	                value = "resetSuccess",
	                required = false
	        ) Boolean resetSuccess,

	        Model model) {

	    if (Boolean.TRUE.equals(resetSuccess)) {

	        model.addAttribute(
	                "success",
	                "Password reset successfully. Please login."
	        );
	    }

	    return "login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute("login") LoginBean login, HttpSession session,
			RedirectAttributes redirectAttributes) {
		LoginBean user = loginRepo.login(login.getEmail(), login.getPassword_hash());
		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
			return "redirect:/login";
		}
		session.setAttribute("roleId", user.getRole_id());
		session.setAttribute("fullName", user.getFull_name());
		session.setAttribute("email", user.getEmail());
		session.setAttribute("password_hash", user.getPassword_hash());
		
		if (user.getRole_id() == 1) {
		    return "redirect:/admin";
		} else if (user.getRole_id() == 2) {
		    return "redirect:/teacher";
		} else if (user.getRole_id() == 3) {
		    return "redirect:/student";
		}

		return "redirect:/login";
//		return "redirect:/dashboard";
	}
	
	
	
	@GetMapping("/teacher")
	public String teacherPage(HttpSession session) {

		if (session.getAttribute("roleId") == null) {
	        return "redirect:/login";
	    }

	    if ((int) session.getAttribute("roleId") != 2) {
	        return "redirect:/login";
	    }

	    return "teacher";
	}
	
	
	@GetMapping("/student")
	public String studentPage(HttpSession session) {

		if (session.getAttribute("roleId") == null) {
	        return "redirect:/login";
	    }

	    if ((int) session.getAttribute("roleId") != 3) {
	        return "redirect:/login";
	    }

	    return "student";
	}
	
	
	

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {

	    Object roleId = session.getAttribute("roleId");

	    if (roleId == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("fullName",
	            session.getAttribute("fullName"));

	    model.addAttribute("email",
	            session.getAttribute("email"));

	    model.addAttribute("roleId",
	            session.getAttribute("roleId"));

	    return "dashboard";
	}
}