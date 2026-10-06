package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    @Autowired
    private Admin_Repository userRepo;


    // =========================================================
    // ADMIN DASHBOARD
    //
    // URL:
    // /admin
    // /admin/
    // /admin/dashboard
    // =========================================================
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            HttpSession session,
            Model model) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Dashboard statistics
        model.addAttribute(
                "totalUsers",
                userRepo.countUsers()
        );

        model.addAttribute(
                "totalRoles",
                userRepo.countRoles()
        );

        model.addAttribute(
                "activeUsers",
                userRepo.countActiveUsers()
        );

        model.addAttribute(
                "inactiveUsers",
                userRepo.countInactiveUsers()
        );

        // File:
        // src/main/resources/templates/dashboard.html
        return "dashboard";
    }


    // =========================================================
    // USER LIST
    //
    // URL:
    // /admin/users
    // =========================================================
    @GetMapping("/users")
    public String users(
            HttpSession session,
            Model model) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Load all users
        model.addAttribute(
                "users",
                userRepo.getAllUsers()
        );

        // File:
        // src/main/resources/templates/users.html
        return "users";
    }


    
    @GetMapping("/users/create")
    public String createForm(
            HttpSession session,
            Model model) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Empty object for form
        model.addAttribute(
                "user",
                new AdminUserBean()
        );

        // File:
        // src/main/resources/templates/user_form.html
        return "user_form";
    }


    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("user") AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Default status
        if (user.getStatus() == null ||
                user.getStatus().isBlank()) {

            user.setStatus("ACTIVE");
        }

        // Save user
        userRepo.createUser(user);

        // Success message
        redirectAttributes.addFlashAttribute(
                "success",
                "User created successfully!"
        );

        // IMPORTANT:
        // Because controller has @RequestMapping("/admin")
        // redirect must contain /admin/users
        return "redirect:/admin/users";
    }


    // =========================================================
    // EDIT USER FORM
    //
    // URL:
    // /admin/users/edit/{id}
    // =========================================================
    @GetMapping("/users/edit/{id}")
    public String editForm(
            @PathVariable("id") int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Get user by ID
        AdminUserBean user =
                userRepo.getUserById(id);

        // User not found
        if (user == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "User not found!"
            );

            return "redirect:/admin/users";
        }

        // Send user to form
        model.addAttribute(
                "user",
                user
        );

        // Same form used for edit
        return "user_form";
    }


    // =========================================================
    // NOTIFICATION
    //
    // URL:
    // /admin/notification
    //
    // File:
    // src/main/resources/templates/notification.html
    // =========================================================
    @GetMapping("/notification")
    public String notification(
            HttpSession session) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        return "notification";
    }


    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Get session data
        model.addAttribute(
                "fullName",
                session.getAttribute("fullName")
        );

        model.addAttribute(
                "email",
                session.getAttribute("email")
        );

        model.addAttribute(
                "roleId",
                session.getAttribute("role_id")
        );

        return "profile";
    }


    @PostMapping("/users/update")
    public String updateUser(
            @ModelAttribute("user") AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Update user
        userRepo.updateUser(user);

        // Success message
        redirectAttributes.addFlashAttribute(
                "success",
                "User updated successfully!"
        );

        return "redirect:/admin/users";
    }


    // =========================================================
    // DELETE USER
    //
    // URL:
    // /admin/users/delete/{id}
    // =========================================================
    @GetMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable("id") int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Check admin login
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Delete user
        userRepo.deleteUser(id);

        // Success message
        redirectAttributes.addFlashAttribute(
                "success",
                "User deleted successfully!"
        );

        return "redirect:/admin/users";
    }


    // =========================================================
    // LOGOUT
    //
    // URL:
    // /admin/logout
    // =========================================================
    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        // Remove all session data
        session.invalidate();

        // Back to login page
        return "redirect:/login";
    }


    // =========================================================
    // CHECK ADMIN
    // =========================================================
    private boolean checkAdmin(
            HttpSession session) {

        // Get role_id from session
        Object role =
                session.getAttribute("role_id");

        // No login session
        if (role == null) {
            return false;
        }

        int roleId;

        try {

            roleId = Integer.parseInt(
                    role.toString()
            );

        } catch (Exception e) {

            return false;
        }

        // Current role setup
        //
        // 1 = Admin
        // 2 = Teacher
        // 3 = Student

        return roleId == 1;
    }
}