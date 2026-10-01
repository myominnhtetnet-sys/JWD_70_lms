package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    @Autowired
    private Admin_Repository adminRepository;

    // 1. Dashboard View
    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(Model model) {
        model.addAttribute("totalUsers", 1248);
        model.addAttribute("activeCourses", 64);
        model.addAttribute("categories", adminRepository.getAllCategories());
        return "dashboard"; 
    }

    // 2. Secondary Admin View
    @GetMapping("/admin")
    public String showAdminView(Model model) {
        model.addAttribute("categories", adminRepository.getAllCategories());
        return "admin";
    }

    
    @GetMapping({"/categories", "/category-list"})
    public String listCategories(Model model) {
        List<AdminBean_category> categories = adminRepository.getAllCategories();
        model.addAttribute("categories", categories);
        return "category-list";
    }

   
    @GetMapping("/categories/new")
    public String showAddForm(Model model) {
        model.addAttribute("category", new AdminBean_category());
        return "category-form";
    }

 
    @PostMapping("/categories/save")
    public String saveCategory(
            @ModelAttribute("category") AdminBean_category category,
            RedirectAttributes redirectAttributes) {

        boolean isNew = category.getCategory_id() <= 0;

        if (isNew) {
            int newId = adminRepository.saveCategory(category);

            redirectAttributes.addFlashAttribute("activeId", newId);
            redirectAttributes.addFlashAttribute("actionType", "new");

        } else {
            adminRepository.updateCategory(category);

            redirectAttributes.addFlashAttribute(
                    "activeId",
                    category.getCategory_id()
            );
        }

        return "redirect:/categories";
    }

   
    @GetMapping("/categories/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        AdminBean_category category = adminRepository.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "category-form";
    }

   
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id) {
        adminRepository.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}