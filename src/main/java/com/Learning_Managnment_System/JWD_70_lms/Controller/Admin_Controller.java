package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    @Autowired
    private Admin_Repository userRepo;

    // =====================================================
    // COMMON ADMIN SESSION CHECK
    // =====================================================

    private boolean checkAdmin(HttpSession session) {

        Object role = session.getAttribute("role_id");

        if (role == null) {
            return false;
        }

        try {
            return Integer.parseInt(role.toString()) == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // =====================================================
    // COMMON ATTRIBUTES
    // =====================================================

    @ModelAttribute
    public void addCommonAttributes(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "fullName",
                session.getAttribute("fullName")
        );

        model.addAttribute(
                "email",
                session.getAttribute("email")
        );
    }

    // =====================================================
    // ADMIN DASHBOARD
    // URL: /admin
    // =====================================================

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("currentPage", "dashboard");

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
        
        // Add this line
        model.addAttribute("roles", userRepo.getAllRoles());

        // Add this line if your Dashboard modal uses adminUser
        model.addAttribute("adminUser", new AdminUserBean());

        

        return "dashboard";
    }

    // =====================================================
    // USER MANAGEMENT
    // URL: /admin/users
    // =====================================================
    @GetMapping("/users")
    public String users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Search and Filter
        List<AdminUserBean> users =
                userRepo.searchUsers(keyword, role, status);

        // Send users to HTML
        model.addAttribute("users", users);

        // Send roles from database to HTML
        model.addAttribute("roles", userRepo.getAllRoles());

        // Form object
        model.addAttribute("adminUser", new AdminUserBean());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", "users");

        model.addAttribute("totalUsers", userRepo.countUsers());
        model.addAttribute("activeUsers", userRepo.countActiveUsers());
        model.addAttribute("inactiveUsers", userRepo.countInactiveUsers());
        model.addAttribute("suspendedUsers", userRepo.countSuspendedUsers());

        return "users";
    }

    // =====================================================
    // CREATE USER
    // URL: POST /admin/users/create
    // =====================================================

    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("adminUser") AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Validate required fields
        if (isBlank(user.getFull_name())
                || isBlank(user.getEmail())
                || isBlank(user.getPassword_hash())
                || user.getRole_id() <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please fill in all required fields."
            );

            return "redirect:/admin/users";
        }

        // Normalize email
        user.setEmail(user.getEmail().trim().toLowerCase());

        // Default status
        if (isBlank(user.getStatus())) {
            user.setStatus("ACTIVE");
        }

        // Validate status
        if (!isValidStatus(user.getStatus())) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid account status."
            );

            return "redirect:/admin/users";
        }

        try {

            int result = userRepo.createUser(user);

            if (result > 0) {

                redirectAttributes.addFlashAttribute(
                        "success",
                        "User created successfully!"
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Unable to create user."
                );
            }

        } catch (DuplicateKeyException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Email or phone number already exists."
            );

        } catch (DataAccessException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Database error while creating user."
            );
        }

        return "redirect:/admin/users";
    }

    // =====================================================
    // EDIT USER FORM
    // URL: GET /admin/users/edit/{id}
    // =====================================================

    @GetMapping("/users/edit/{id}")
    public String editUser(
            @PathVariable int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        AdminUserBean user = userRepo.getUserById(id);

        if (user == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "User not found."
            );

            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);

        model.addAttribute(
                "roles",
                userRepo.getAllRoles()
        );

        model.addAttribute("currentPage", "users");

        return "user_form";
    }

    // =====================================================
    // UPDATE USER
    // URL: POST /admin/users/update
    // =====================================================

    @PostMapping("/users/update")
    public String updateUser(
            @ModelAttribute("user") AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (user.getUser_id() <= 0
                || isBlank(user.getFull_name())
                || isBlank(user.getEmail())
                || user.getRole_id() <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please provide valid user information."
            );

            return "redirect:/admin/users";
        }

        user.setEmail(user.getEmail().trim().toLowerCase());

        if (!isValidStatus(user.getStatus())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid account status."
            );

            return "redirect:/admin/users";
        }

        try {

            int result = userRepo.updateUser(user);

            if (result > 0) {

                redirectAttributes.addFlashAttribute(
                        "success",
                        "User updated successfully!"
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "User not found or already deleted."
                );
            }

        } catch (DuplicateKeyException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Email or phone number already exists."
            );

        } catch (DataAccessException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Database error while updating user."
            );
        }

        return "redirect:/admin/users";
    }

    // =====================================================
    // VIEW USER
    // URL: GET /admin/users/view/{id}
    // =====================================================

    @GetMapping("/users/view/{id}")
    public String viewUser(
            @PathVariable int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        AdminUserBean user = userRepo.getUserById(id);

        if (user == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "User not found."
            );

            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        model.addAttribute("currentPage", "users");

        return "user_view";
    }

    // =====================================================
    // CHANGE STATUS
    // URL: POST /admin/users/status/{id}
    // =====================================================

    @PostMapping("/users/status/{id}")
    public String changeStatus(
            @PathVariable int id,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (!isValidStatus(status)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid account status."
            );

            return "redirect:/admin/users";
        }

        // Do not suspend or deactivate the currently logged-in admin.
        Object loggedInId = session.getAttribute("user_id");

        if (loggedInId != null
                && loggedInId.toString().equals(String.valueOf(id))
                && !"ACTIVE".equals(status)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "You cannot deactivate your own account."
            );

            return "redirect:/admin/users";
        }

        int result = userRepo.updateUserStatus(id, status);

        if (result > 0) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "User status updated successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "User not found or already deleted."
            );
        }

        return "redirect:/admin/users";
    }

    // =====================================================
    // SOFT DELETE
    // URL: POST /admin/users/delete/{id}
    // =====================================================

    @PostMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        Object loggedInId = session.getAttribute("user_id");

        if (loggedInId != null
                && loggedInId.toString().equals(String.valueOf(id))) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "You cannot delete your own account."
            );

            return "redirect:/admin/users";
        }

        int result = userRepo.deleteUser(id);

        if (result > 0) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "User deleted successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "User not found or already deleted."
            );
        }

        return "redirect:/admin/users";
    }

    // =====================================================
    // NOTIFICATION
    // =====================================================

    @GetMapping("/notification")
    public String notification(HttpSession session) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        return "notification";
    }

    // =====================================================
    // PROFILE
    // =====================================================

    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("currentPage", "profile");

        return "profile";
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    // =====================================================
    // VALIDATION HELPERS
    // =====================================================

    private boolean isBlank(String value) {

        return value == null || value.isBlank();
    }

    private boolean isValidStatus(String status) {

        return "ACTIVE".equals(status)
                || "INACTIVE".equals(status)
                || "SUSPENDED".equals(status);
    }
}