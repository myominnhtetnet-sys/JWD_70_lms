package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AdminProfileRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AdminProfileController {

    @Autowired
    private AdminProfileRepository adminProfileRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin/profile")
    public String viewProfile(HttpSession session, Model model) {
        Object roleId = session.getAttribute("roleId");
        if (roleId == null || (int) roleId != 1) { // 1 = Admin Role
            return "redirect:/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Map<String, Object> admin = adminProfileRepo.getUserById(userId);
        if (admin != null) {
            model.addAttribute("fullName", admin.get("full_name"));
            model.addAttribute("email", admin.get("email"));
            model.addAttribute("phone", admin.get("phone"));
            model.addAttribute("address", admin.get("address"));
        }

        return "admin_profile";
    }

    @PostMapping("/admin/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "address", required = false) String address,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        adminProfileRepo.updateProfile(userId, fullName, phone, address);
        session.setAttribute("fullName", fullName); // update session name instantly
        
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/admin/profile";
    }

    @PostMapping("/admin/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/admin/profile";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Map<String, Object> admin = adminProfileRepo.getUserById(userId);
        if (admin != null) {
            String storedPasswordHash = (String) admin.get("password_hash");

            if (passwordEncoder.matches(currentPassword, storedPasswordHash)) {
                String newHashedPassword = passwordEncoder.encode(newPassword);
                adminProfileRepo.updatePassword(userId, newHashedPassword);
                redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Incorrect current password.");
            }
        }

        return "redirect:/admin/profile";
    }
}