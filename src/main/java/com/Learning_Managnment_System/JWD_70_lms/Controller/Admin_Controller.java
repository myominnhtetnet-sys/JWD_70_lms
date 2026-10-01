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
    private Admin_Repository categoryRepository;

 
    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(Model model) {
        model.addAttribute("totalUsers", 1248);
        model.addAttribute("activeCourses", 64);
        model.addAttribute("categories", categoryRepository.getAllCategories());

        // Assumes template is at: src/main/resources/templates/dashboard.html
        return "dashboard"; 
    }

  
    @GetMapping("/admin-view")
    public String showAdminView(Model model) {
        model.addAttribute("categories", categoryRepository.getAllCategories());

        
        return "admin-view";
    }

 
    @GetMapping({"/categories", "/category-list"})
    public String listCategories(Model model) {
        List<AdminBean_category> categories = categoryRepository.getAllCategories();
        model.addAttribute("categories", categories);
        return "category-list";
    }

    
    @GetMapping("/categories/new")
    public String showAddForm(Model model) {
        model.addAttribute("category", new AdminBean_category());
        return "category-form";
    }

  
    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") AdminBean_category category, RedirectAttributes redirectAttributes) {
        boolean isNew = (category.getCategory_id() == null || category.getCategory_id() <= 0);

        if (isNew) {
            int newId = categoryRepository.saveCategory(category);
            redirectAttributes.addFlashAttribute("activeId", newId);
            redirectAttributes.addFlashAttribute("actionType", "new");
        } else {
            categoryRepository.updateCategory(category);
            redirectAttributes.addFlashAttribute("activeId", category.getCategory_id());
            redirectAttributes.addFlashAttribute("actionType", "edited");
        }

        return "redirect:/admin/categories";
    }

    // 6. Show Edit Category Form
    @GetMapping("/categories/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        AdminBean_category category = categoryRepository.getCategoryById(id);
        model.addAttribute("category", category);
        return "category-form";
    }

    // 7. Delete Category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id) {
        categoryRepository.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}