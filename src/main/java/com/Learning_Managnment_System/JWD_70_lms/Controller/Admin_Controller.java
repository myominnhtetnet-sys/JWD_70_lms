package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
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
//
//@GetMapping("/dashboard")
//public String adminDashboard(HttpSession session, Model model) {
//if (!checkAdmin(session)) return "redirect:/login";
//model.addAttribute("fullName", session.getAttribute("fullName"));
//return "dashboard";
//}

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
public String listUsers(HttpSession session, Model model) {
    if (!checkAdmin(session)) return "redirect:/login";

    // getUserById() အစား getUsers() (သို့မဟုတ် ဇယားအချက်အလက်အားလုံးယူသည့် method) သို့ ပြောင်းပါ
    model.addAttribute("users", userRepo.getUsers()); 
    model.addAttribute("user", new AdminUserBean());
    model.addAttribute("fullName", session.getAttribute("fullName"));
    return "users";
}

@PostMapping("/users/create")
public String createUser(@ModelAttribute AdminUserBean user, HttpSession session, RedirectAttributes ra) {
if (!checkAdmin(session)) return "redirect:/login";

userRepo.createUser(user);
ra.addFlashAttribute("success", "User created successfully.");
return "redirect:/admin/users";
}

@PostMapping("/users/status")
public String updateStatus(@RequestParam int userId, @RequestParam String status, HttpSession session, RedirectAttributes ra) {
if (!checkAdmin(session)) return "redirect:/login";

userRepo.updateStatus(userId, status);
ra.addFlashAttribute("success", "User status updated successfully.");
return "redirect:/admin/users";
}
}