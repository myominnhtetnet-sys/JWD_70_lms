package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
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

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        model.addAttribute("fullName", session.getAttribute("fullName"));
        return "dashboard"; 
    }

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

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

    // Edit လုပ်ရန် User အချက်အလက် JSON ဖြင့် ထုတ်ပေးခြင်း
    @GetMapping("/users/edit/{id}")
    @ResponseBody
    public AdminUserBean getUserForEdit(@PathVariable int id, HttpSession session) {
        if (!checkAdmin(session)) return null;
        return userRepo.getUserById(id);
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute AdminUserBean user, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";

        userRepo.updateUser(user);
        ra.addFlashAttribute("success", "User updated successfully.");
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