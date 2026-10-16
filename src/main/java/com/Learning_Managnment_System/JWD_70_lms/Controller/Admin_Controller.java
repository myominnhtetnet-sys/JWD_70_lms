package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    private static final Logger log = LoggerFactory.getLogger(Admin_Controller.class);

    @Autowired
    private Admin_Repository adminRepository;

    private boolean checkAdmin(HttpSession session) {
        Object role = session.getAttribute("role_id");
        return role != null && Integer.parseInt(role.toString()) == 1;
    }

    // ==================== DASHBOARD ====================

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        
        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("totalUsers", 1248);
        model.addAttribute("activeCourses", 64);
        model.addAttribute("categories", adminRepository.getAllCategories());
        
        return "dashboard";
    }

    @GetMapping("/admin-view")
    public String showAdminView(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        model.addAttribute("categories", adminRepository.getAllCategories());
        return "admin-view";
    }

    // ==================== CATEGORY MANAGEMENT ====================

    @GetMapping({"/category", "/categories"})
    public String categoryIndex(HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/login";
        return "redirect:/admin/category/list";
    }

    @GetMapping({"/category/list", "/categories/list"})
    public String getAllCategories(
            @ModelAttribute("editedId") String editedIdStr,
            @ModelAttribute("createdId") String createdIdStr,
            HttpSession session,
            Model model) {
        
        if (!checkAdmin(session)) return "redirect:/login";

        List<AdminBean_category> categoryList = adminRepository.getAllCategories();

        if (categoryList != null && !categoryList.isEmpty()) {
            if (editedIdStr != null && !editedIdStr.isEmpty()) {
                try {
                    int editedId = Integer.parseInt(editedIdStr);
                    categoryList.stream()
                            .filter(c -> c.getCategory_id() != null && c.getCategory_id() == editedId)
                            .findFirst()
                            .ifPresent(c -> {
                                c.set_edited(true);
                                c.set_new(false);
                            });
                } catch (NumberFormatException e) {
                    log.warn("Invalid editedId flash attribute: {}", editedIdStr);
                }
            }
            if (createdIdStr != null && !createdIdStr.isEmpty()) {
                try {
                    int createdId = Integer.parseInt(createdIdStr);
                    categoryList.stream()
                            .filter(c -> c.getCategory_id() != null && c.getCategory_id() == createdId)
                            .findFirst()
                            .ifPresent(c -> {
                                c.set_new(true);
                                c.set_edited(false);
                            });
                } catch (NumberFormatException e) {
                    log.warn("Invalid createdId flash attribute: {}", createdIdStr);
                }
            }
            model.addAttribute("categoryResults", categoryList);
        } else {
            model.addAttribute("notFoundMessage", "No category records found.");
            model.addAttribute("categoryResults", List.of());
        }

        return "category-list";
    }

    @GetMapping({"/category/create", "/categories/create"})
    public String showCreateForm(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        AdminBean_category category = new AdminBean_category();
        category.setIs_active(1); // Default active

        model.addAttribute("category", category);
        model.addAttribute("parentCategories", adminRepository.getAllCategories());

        return "category-form";
    }

    @GetMapping({"/category/edit/{id}", "/categories/edit/{id}"})
    public String showEditForm(
            @PathVariable("id") int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (!checkAdmin(session)) return "redirect:/login";

        AdminBean_category category = adminRepository.getCategoryById(id);
        if (category == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category record not found.");
            return "redirect:/admin/category/list";
        }

        model.addAttribute("category", category);
        model.addAttribute("parentCategories", adminRepository.getAllCategories());

        return "category-form";
    }

    @PostMapping({"/category/save", "/categories/save"})
    public String saveCategory(
            @ModelAttribute("category") AdminBean_category category,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) return "redirect:/login";

        if (category.getIs_active() == null) {
            category.setIs_active(1);
        }

        if (category.getCategory_id() != null && category.getCategory_id() > 0) {
            int rowsAffected = adminRepository.updateCategory(category);
            if (rowsAffected > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
                redirectAttributes.addFlashAttribute("editedId", String.valueOf(category.getCategory_id()));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update category.");
            }
        } else {
            int newId = adminRepository.saveCategory(category);
            if (newId > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "Category created successfully.");
                redirectAttributes.addFlashAttribute("createdId", String.valueOf(newId));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create category.");
            }
        }

        return "redirect:/admin/category/list";
    }

    @GetMapping({"/category/delete/{id}", "/categories/delete/{id}"})
    public String deleteCategory(
            @PathVariable("id") int id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (!checkAdmin(session)) return "redirect:/login";

        int rowsAffected = adminRepository.deleteCategory(id);
        if (rowsAffected > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete category.");
        }

        return "redirect:/admin/category/list";
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        model.addAttribute("users", adminRepository.getUsers()); 
        model.addAttribute("user", new AdminUserBean());
        model.addAttribute("fullName", session.getAttribute("fullName"));
        
        return "users";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute AdminUserBean user, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";

        adminRepository.createUser(user);
        ra.addFlashAttribute("success", "User created successfully.");
        
        return "redirect:/admin/users";
    }

    @PostMapping("/users/status")
    public String updateStatus(@RequestParam int userId, @RequestParam String status, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";

        adminRepository.updateStatus(userId, status);
        ra.addFlashAttribute("success", "User status updated successfully.");
        
        return "redirect:/admin/users";
    }
}