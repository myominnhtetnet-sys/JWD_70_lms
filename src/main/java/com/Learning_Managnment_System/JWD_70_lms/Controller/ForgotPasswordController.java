package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.LoginRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ForgotPasswordBean;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {

    @Autowired
    private LoginRepository loginRepo;

    // =====================================================
    // 1. Show Forgot Password Page
    // =====================================================

    @GetMapping("/forgot-password")
    public String showForgotPassword(Model model) {

        model.addAttribute(
                "forgotPassword",
                new ForgotPasswordBean()
        );

        return "forgot_password";
    }

    // =====================================================
    // 2. Check Email + Generate OTP
    // =====================================================

    @PostMapping("/forgot-password")
    public String sendCode(
            @ModelAttribute("forgotPassword")
            ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        String email = forgotPassword.getEmail();

        // Check empty email
        if (email == null || email.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please enter your email."
            );

            return "forgot_password";
        }

        email = email.trim();

        // Check email exists
        boolean exists = loginRepo.emailExists(email);

        if (!exists) {

            model.addAttribute(
                    "error",
                    "This email is not registered."
            );

            return "forgot_password";
        }

        // Generate 6 digit OTP
        Random random = new Random();

        String code = String.format(
                "%06d",
                random.nextInt(1000000)
        );

        // Save email and OTP in session
        session.setAttribute(
                "resetEmail",
                email
        );

        session.setAttribute(
                "resetCode",
                code
        );

        // OTP expires after 5 minutes
        long expiryTime =
                System.currentTimeMillis()
                + (5 * 60 * 1000);

        session.setAttribute(
                "resetCodeExpiry",
                expiryTime
        );

        // Demo: show OTP on website
        model.addAttribute(
                "generatedCode",
                code
        );

        model.addAttribute(
                "email",
                email
        );

        model.addAttribute(
                "forgotPassword",
                new ForgotPasswordBean()
        );

        return "verify_code";
    }

    // =====================================================
    // 3. Show Verify Code Page
    // =====================================================

    @GetMapping("/verify-code")
    public String verifyCodePage(
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute("resetEmail");

        String code =
                (String) session.getAttribute("resetCode");

        if (email == null || code == null) {
            return "redirect:/forgot-password";
        }

        model.addAttribute(
                "email",
                email
        );

        model.addAttribute(
                "generatedCode",
                code
        );

        model.addAttribute(
                "forgotPassword",
                new ForgotPasswordBean()
        );

        return "verify_code";
    }

    // =====================================================
    // 4. Verify OTP
    // =====================================================

    @PostMapping("/verify-code")
    public String verifyCode(
            @ModelAttribute("forgotPassword")
            ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        String enteredCode =
                forgotPassword.getCode();

        String sessionCode =
                (String) session.getAttribute("resetCode");

        Long expiry =
                (Long) session.getAttribute("resetCodeExpiry");

        String email =
                (String) session.getAttribute("resetEmail");

        // Check session
        if (sessionCode == null
                || expiry == null
                || email == null) {

            return "redirect:/forgot-password";
        }

        // Check expiry
        if (System.currentTimeMillis() > expiry) {

            session.removeAttribute("resetCode");
            session.removeAttribute("resetCodeExpiry");

            model.addAttribute(
                    "error",
                    "Verification code has expired. Please try again."
            );

            model.addAttribute(
                    "email",
                    email
            );

            model.addAttribute(
                    "forgotPassword",
                    new ForgotPasswordBean()
            );

            return "verify_code";
        }

        // Check empty OTP
        if (enteredCode == null
                || enteredCode.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please enter the verification code."
            );

            model.addAttribute(
                    "email",
                    email
            );

            model.addAttribute(
                    "generatedCode",
                    sessionCode
            );

            model.addAttribute(
                    "forgotPassword",
                    new ForgotPasswordBean()
            );

            return "verify_code";
        }

        // Compare OTP
        if (!sessionCode.equals(enteredCode.trim())) {

            model.addAttribute(
                    "error",
                    "Invalid verification code."
            );

            model.addAttribute(
                    "email",
                    email
            );

            model.addAttribute(
                    "generatedCode",
                    sessionCode
            );

            return "verify_code";
        }

        // OTP correct
        session.setAttribute(
                "codeVerified",
                true
        );

        // Remove OTP after successful verification
        session.removeAttribute("resetCode");
        session.removeAttribute("resetCodeExpiry");

        // Go to reset password
        return "redirect:/reset-password";
    }

    // =====================================================
    // 5. Show Reset Password Page
    // =====================================================

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            HttpSession session,
            Model model) {

        Boolean verified =
                (Boolean) session.getAttribute("codeVerified");

        String email =
                (String) session.getAttribute("resetEmail");

        // Security check
        if (!Boolean.TRUE.equals(verified)
                || email == null) {

            return "redirect:/forgot-password";
        }

        model.addAttribute(
                "forgotPassword",
                new ForgotPasswordBean()
        );

        model.addAttribute(
                "email",
                email
        );

        return "reset_password";
    }

    // =====================================================
    // 6. Update New Password
    // =====================================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @ModelAttribute("forgotPassword")
            ForgotPasswordBean forgotPassword,
            HttpSession session,
            Model model) {

        Boolean verified =
                (Boolean) session.getAttribute("codeVerified");

        String email =
                (String) session.getAttribute("resetEmail");

        // Security check
        if (!Boolean.TRUE.equals(verified)
                || email == null) {

            return "redirect:/forgot-password";
        }

        String newPassword =
                forgotPassword.getNewPassword();

        String confirmPassword =
                forgotPassword.getConfirmPassword();

        // Check empty password
        if (newPassword == null
                || newPassword.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please enter a new password."
            );

            model.addAttribute(
                    "email",
                    email
            );

            return "reset_password";
        }

        // Check password length
        if (newPassword.length() < 6) {

            model.addAttribute(
                    "error",
                    "Password must be at least 6 characters."
            );

            model.addAttribute(
                    "email",
                    email
            );

            return "reset_password";
        }

        // Check confirm password
        if (confirmPassword == null
                || !newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            model.addAttribute(
                    "email",
                    email
            );

            return "reset_password";
        }

        // Update database
        int result =
                loginRepo.resetPassword(
                        email,
                        newPassword
                );

        // Success
        if (result > 0) {

            session.removeAttribute("resetEmail");
            session.removeAttribute("codeVerified");

            return "redirect:/login?resetSuccess=true";
        }

        // Failed
        model.addAttribute(
                "error",
                "Password reset failed."
        );

        model.addAttribute(
                "email",
                email
        );

        return "reset_password";
    }
}