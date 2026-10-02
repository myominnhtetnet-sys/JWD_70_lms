package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.LoginRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ForgotPasswordBean;

@Controller

public class ForgotPasswordController {
	@Autowired
	private LoginRepository loginRepo;

	@GetMapping("/forgot-password")
	public String showForgotPassword(Model model) {
		model.addAttribute("forgotPassword", new ForgotPasswordBean());
		return "forgot_password";
	}

	@PostMapping("/forgot-password")
	public String resetPassword(@ModelAttribute("forgotPassword") ForgotPasswordBean forgotPassword, Model model) {
		String email = forgotPassword.getEmail();
		String newPassword = forgotPassword.getNewPassword();
		String confirmPassword = forgotPassword.getConfirmPassword();
		if (email == null || email.trim().isEmpty()) {
			model.addAttribute("error", "Please enter your email.");
			return "forgot_password";
		}
		if (!loginRepo.emailExists(email)) {
			model.addAttribute("error", "Email address not found.");
			return "forgot_password";
		}
		if (newPassword == null || newPassword.trim().isEmpty()) {
			model.addAttribute("error", "Please enter a new password.");
			return "forgot_password";
		}
		
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match.");
			return "forgot_password";
		}
		int result = loginRepo.resetPassword(email, newPassword);
		if (result > 0) {
			model.addAttribute("success", "Password reset successfully. Please login.");
			return "login";
		}
		model.addAttribute("error", "Password reset failed.");
		return "forgot_password";
	}
}