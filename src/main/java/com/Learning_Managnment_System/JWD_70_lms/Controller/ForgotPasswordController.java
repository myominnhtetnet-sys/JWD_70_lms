package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.LoginRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.EmailService;
import com.Learning_Managnment_System.JWD_70_lms.model.ForgotPasswordBean;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {

    @Autowired
    private LoginRepository loginRepo;

    @Autowired
    private EmailService emailService;

    // =====================================================
    // 1. Show Forgot Password Page
    // =====================================================
    @GetMapping("/forgot-password")
    public String showForgotPassword(Model model) {
        model.addAttribute("forgotPassword", new ForgotPasswordBean());
        return "forgot_password";
    }

    // =====================================================
    // 2. Check Email + Generate & Send Real OTP via Email
    // =====================================================
    @PostMapping("/forgot-password")
    public String sendCode(
            @ModelAttribute("forgotPassword") ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        String email = forgotPassword.getEmail();

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your email.");
            return "forgot_password";
        }

        email = email.trim();

        boolean exists = loginRepo.emailExists(email);

        if (!exists) {
            model.addAttribute("error", "This email is not registered.");
            return "forgot_password";
        }

        // Generate 6 digit OTP
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));

        // Save session values
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetCode", code);

        // OTP expires after 5 minutes
        long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000);
        session.setAttribute("resetCodeExpiry", expiryTime);

        // Send actual email
        try {
            emailService.sendVerificationEmail(email, code);
        } catch (Exception e) {
            e.printStackTrace(); // 🔴 This will print the real reason in your console!
            model.addAttribute("error", "Error: " + e.getMessage());
            return "forgot_password";
        }

        return "redirect:/verify-code";
    }

    // =====================================================
    // 3. Show Verify Code Page
    // =====================================================
    @GetMapping("/verify-code")
    public String verifyCodePage(
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("resetEmail");
        String code = (String) session.getAttribute("resetCode");

        if (email == null || code == null) {
            return "redirect:/forgot-password";
        }

        model.addAttribute("email", email);
        model.addAttribute("forgotPassword", new ForgotPasswordBean());

        return "verify_code";
    }

    // =====================================================
    // 4. Verify OTP
    // =====================================================
    @PostMapping("/verify-code")
    public String verifyCode(
            @ModelAttribute("forgotPassword") ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        String enteredCode = forgotPassword.getCode();
        String sessionCode = (String) session.getAttribute("resetCode");
        Long expiry = (Long) session.getAttribute("resetCodeExpiry");
        String email = (String) session.getAttribute("resetEmail");

        if (sessionCode == null || expiry == null || email == null) {
            return "redirect:/forgot-password";
        }

        // Check expiry
        if (System.currentTimeMillis() > expiry) {
            session.removeAttribute("resetCode");
            session.removeAttribute("resetCodeExpiry");

            model.addAttribute("error", "Verification code has expired. Please try again.");
            model.addAttribute("email", email);
            model.addAttribute("forgotPassword", new ForgotPasswordBean());

            return "verify_code";
        }

        // Check empty OTP
        if (enteredCode == null || enteredCode.trim().isEmpty()) {
            model.addAttribute("error", "Please enter the verification code.");
            model.addAttribute("email", email);
            model.addAttribute("forgotPassword", new ForgotPasswordBean());

            return "verify_code";
        }

        // Compare OTP
        if (!sessionCode.equals(enteredCode.trim())) {
            model.addAttribute("error", "Invalid verification code.");
            model.addAttribute("email", email);
            model.addAttribute("forgotPassword", new ForgotPasswordBean());

            return "verify_code";
        }

        // OTP correct
        session.setAttribute("codeVerified", true);

        // Remove OTP fields after successful verification
        session.removeAttribute("resetCode");
        session.removeAttribute("resetCodeExpiry");

        return "redirect:/reset-password";
    }

    // =====================================================
    // 5. Show Reset Password Page
    // =====================================================
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            HttpSession session,
            Model model) {

        Boolean verified = (Boolean) session.getAttribute("codeVerified");
        String email = (String) session.getAttribute("resetEmail");

        if (!Boolean.TRUE.equals(verified) || email == null) {
            return "redirect:/forgot-password";
        }

        model.addAttribute("forgotPassword", new ForgotPasswordBean());
        model.addAttribute("email", email);

        return "reset_password";
    }

    // =====================================================
    // 6. Update New Password
    // =====================================================
    @PostMapping("/reset-password")
    public String resetPassword(
            @ModelAttribute("forgotPassword") ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        Boolean verified = (Boolean) session.getAttribute("codeVerified");
        String email = (String) session.getAttribute("resetEmail");

        if (!Boolean.TRUE.equals(verified) || email == null) {
            return "redirect:/forgot-password";
        }

        String newPassword = forgotPassword.getNewPassword();
        String confirmPassword = forgotPassword.getConfirmPassword();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a new password.");
            model.addAttribute("email", email);
            return "reset_password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            model.addAttribute("email", email);
            return "reset_password";
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("email", email);
            return "reset_password";
        }

        // 🟢 FIXED: Fetch the shared encoding bean from LoginRepository and hash the plain text password string before saving
        String secureHashedPassword = loginRepo.getPasswordEncoder().encode(newPassword.trim());

        // 🟢 FIXED: Update database using the securely generated hash string instead of the raw text
        int result =
                loginRepo.resetPassword(
                        email,
                        secureHashedPassword
                );

        if (result > 0) {
            session.removeAttribute("resetEmail");
            session.removeAttribute("codeVerified");
            return "redirect:/login?resetSuccess=true";
        }

        model.addAttribute("error", "Password reset failed.");
        model.addAttribute("email", email);

        return "reset_password";
    }

}