package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {
	
	@GetMapping("/")
	public String show() {		
		return "dashboard";		
	}
	


    @Autowired
    private Admin_Repository adminRepository;

    @GetMapping("/")
    public String show() {        
        return "dashboard";        
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        List<AdminUserBean> users = adminRepository.getAllUsers();
        model.addAttribute("users", users);

        return "dashboard";
    }

    // =========================================================
    // SEARCH USER
    // =========================================================

    @GetMapping("/users/search")
    public String searchUsers(
            @RequestParam("keyword") String keyword,
            Model model) {

        List<AdminUserBean> users;

        if (keyword == null || keyword.trim().isEmpty()) {
            users = adminRepository.getAllUsers();
        } else {
            users = adminRepository.searchUsers(keyword);
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);

        return "dashboard";
    }

    // =========================================================
    // SHOW ADD USER FORM
    // =========================================================

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {

        model.addAttribute("user", new AdminUserBean());
        return "admin/user_form";
    }

    // =========================================================
    // ADD USER
    // =========================================================

    @PostMapping("/users/add")
    public String addUser(
            AdminUserBean user,
            RedirectAttributes redirectAttributes) {

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Full name is required.");
            return "redirect:/admin/users/add";
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email is required.");
            return "redirect:/admin/users/add";
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password is required.");
            return "redirect:/admin/users/add";
        }

        if (adminRepository.emailExists(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "This email is already registered.");
            return "redirect:/admin/users/add";
        }

        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus("ACTIVE");
        }

        int result = adminRepository.addUser(user);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User added successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to add user.");
        }

        return "redirect:/admin/";
    }

    // =========================================================
    // VIEW USER
    // =========================================================

    @GetMapping("/users/view/{id}")
    public String viewUser(
            @PathVariable("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        AdminUserBean user = adminRepository.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/";
        }

        model.addAttribute("user", user);
        return "admin/user_view";
    }

    // =========================================================
    // SHOW EDIT USER FORM
    // =========================================================

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(
            @PathVariable("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        AdminUserBean user = adminRepository.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/";
        }

        model.addAttribute("user", user);
        return "admin/user_form";
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @PostMapping("/users/update")
    public String updateUser(
            AdminUserBean user,
            RedirectAttributes redirectAttributes) {

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Full name is required.");
            return "redirect:/admin/users/edit/" + user.getUserId();
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email is required.");
            return "redirect:/admin/users/edit/" + user.getUserId();
        }

        if (adminRepository.emailExistsForOtherUser(user.getEmail(), user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "This email is already used by another user.");
            return "redirect:/admin/users/edit/" + user.getUserId();
        }

        int result = adminRepository.updateUser(user);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update user.");
        }

        return "redirect:/admin/";
    }

    // =========================================================
    // SUSPEND USER
    // =========================================================

    @PostMapping("/users/suspend")
    public String suspendUser(
            @RequestParam("userId") int userId,
            RedirectAttributes redirectAttributes) {

        int result = adminRepository.updateStatus(userId, "SUSPENDED");

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User suspended successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to suspend user.");
        }

        return "redirect:/admin/";
    }

    // =========================================================
    // ACTIVATE USER
    // =========================================================

    @PostMapping("/users/activate")
    public String activateUser(
            @RequestParam("userId") int userId,
            RedirectAttributes redirectAttributes) {

        int result = adminRepository.updateStatus(userId, "ACTIVE");

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User activated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to activate user.");
        }

        return "redirect:/admin/";
    }

    // =========================================================
    // DELETE USER - SOFT DELETE
    // =========================================================

    @PostMapping("/users/delete")
    public String deleteUser(
            @RequestParam("userId") int userId,
            RedirectAttributes redirectAttributes) {

        int result = adminRepository.deleteUser(userId);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to delete user.");
        }

        return "redirect:/admin/";
    }
}