package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.Service.DashboardService;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

@Autowired
private Admin_Repository userRepo;

private boolean checkAdmin(HttpSession session) {
Object role = session.getAttribute("role_id");
return role != null && Integer.parseInt(role.toString()) == 1;
}

@Autowired
private DashboardService dashboardService;
@GetMapping("/dashboard")
public String showDashboard(Model model) {
    model.addAttribute("activePage", "dashboard");
    model.addAttribute("banner", dashboardService.getBannerSummary());
    model.addAttribute("stats", dashboardService.getStatCardsSummary());
    model.addAttribute("pendingPayments", dashboardService.getPendingPayments());
    model.addAttribute("batches", dashboardService.getBatchSeatProgress());
    model.addAttribute("overdues", dashboardService.getOverdueInstallmentsList());
    return "dashboard";
}
@GetMapping("/users")
public String listUsers(
        @RequestParam(required = false) String keyword,
        HttpSession session,
        Model model) {

    if (!checkAdmin(session)) {
        return "redirect:/login";
    }

    // Remove leading/trailing whitespace, including tabs
    String searchKeyword = keyword == null ? "" : keyword.trim();

    if (searchKeyword.isBlank()) {
        model.addAttribute("users", userRepo.getUsers());
    } else {
        model.addAttribute("users", userRepo.searchUsers(searchKeyword));
    }

    model.addAttribute("keyword", searchKeyword);
    model.addAttribute("user", new AdminUserBean());
    model.addAttribute("fullName", session.getAttribute("fullName"));

    return "users";
}

@PostMapping("/users/create")
public String createUser(
        @ModelAttribute AdminUserBean user,
        HttpSession session,
        RedirectAttributes ra) {

    if (!checkAdmin(session)) {
        return "redirect:/login";
    }

    if (user.getPassword() == null ||
            user.getPassword().isBlank()) {
        ra.addFlashAttribute("error", "Password is required.");
        return "redirect:/admin/users";
    }

    // Current plain-text setup
    user.setPassword_hash(user.getPassword());

    userRepo.createUser(user);

    ra.addFlashAttribute(
        "success", "User created successfully."
    );

    return "redirect:/admin/users";
}


@PostMapping("/users/status")
public String updateStatus(@RequestParam int userId, @RequestParam String status, HttpSession session, RedirectAttributes ra) {
if (!checkAdmin(session)) return "redirect:/login";

userRepo.updateStatus(userId, status);
ra.addFlashAttribute("success", "User status updated successfully.");
return "redirect:/admin/users";
}


@GetMapping("/users/edit/{id}")
@ResponseBody
public ResponseEntity<AdminUserBean> getUserForEdit(
        @PathVariable("id") int id,
        HttpSession session) {

    if (!checkAdmin(session)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    AdminUserBean user = userRepo.getUserById(id);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(user);
}

@PostMapping("/users/update")
public String updateUser(
        @ModelAttribute AdminUserBean user,
        HttpSession session,
        RedirectAttributes ra) {

    if (!checkAdmin(session)) {
        return "redirect:/login";
    }

    AdminUserBean existingUser =
            userRepo.getUserById(user.getUser_id());

    if (existingUser == null) {
        ra.addFlashAttribute("error", "User not found.");
        return "redirect:/admin/users";
    }

    int result = userRepo.updateUser(user);

    if (result > 0) {
        ra.addFlashAttribute(
            "success", "User updated successfully."
        );
    } else {
        ra.addFlashAttribute(
            "error", "User could not be updated."
        );
    }

    return "redirect:/admin/users";
}
}