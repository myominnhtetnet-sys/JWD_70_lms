package com.Learning_Managnment_System.JWD_70_lms.Controller;


import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Respority;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    @Autowired
    private Admin_Respority adminRepository;

   
    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(Model model) {

        model.addAttribute("totalUsers", 1248);
        model.addAttribute("activeCourses", 64);

        model.addAttribute(
                "categories",
                adminRepository.getAllCategories()
        );

        return "dashboard";
    }

    // =========================================
    // CATEGORY LIST
    // URLs: /admin/categories, /admin/categories/list
    // =========================================
    @GetMapping({"/categories", "/categories/list"})
    public String listCategories(Model model) {

        List<AdminBean_category> categories =
                adminRepository.getAllCategories();

        model.addAttribute("categories", categories);

        return "category-list";
    }

    // =========================================
    // REDIRECT OLD NEW-CATEGORY URL TO LIST
    // URL: /admin/categories/new
    // =========================================
    @GetMapping("/categories/new")
    public String redirectToCategoryList() {
        return "redirect:/admin/categories/list";
    }


    @PostMapping("/categories/save")
    public String saveCategory(
            @ModelAttribute("category")
            AdminBean_category category,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            boolean isNew =
                    category.getCategory_id() == null
                    || category.getCategory_id() <= 0;

            if (isNew) {

                int newId =
                        adminRepository.saveCategory(category);

                redirectAttributes.addFlashAttribute(
                        "activeId", newId
                );

                redirectAttributes.addFlashAttribute(
                        "actionType", "NEW"
                );

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Category created successfully."
                );

            } else {

                adminRepository.updateCategory(category);

                redirectAttributes.addFlashAttribute(
                        "activeId",
                        category.getCategory_id()
                );

                redirectAttributes.addFlashAttribute(
                        "actionType", "EDITED"
                );

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Category updated successfully."
                );
            }

            return "redirect:/admin/categories/list";

        } catch (DuplicateKeyException e) {

            model.addAttribute(
                    "errorMessage",
                    "The category name or slug already exists."
            );

            model.addAttribute(
                    "parentCategories",
                    adminRepository.getAllCategories()
            );

            return "category-form";
        }
    }

  
    @GetMapping("/categories/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model) {

        AdminBean_category category =
                adminRepository.getCategoryById(id);

        if (category == null) {
            return "redirect:/admin/categories/list";
        }

        model.addAttribute("category", category);

        model.addAttribute(
                "parentCategories",
                adminRepository.getAllCategories()
        );

        return "category-form";
    }

    // =========================================
    // DELETE CATEGORY
    // URL: /admin/categories/delete/{id}
    // =========================================
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        adminRepository.deleteCategory(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Category deleted successfully."
        );

        return "redirect:/admin/categories/list";
    }

    // =========================================
    // DISCOUNT PAGE
    // URL: /admin/discounts
    // IMPORTANT: Keep only ONE mapping for this URL.
    // =========================================
    @GetMapping("/discounts")
    public String showDiscountsPage() {
        return "discount-list";
    }

    // =========================================
    // DASHBOARD LIST PAGE
    // URL: /admin/dashboard-list
    // =========================================
    @GetMapping("/dashboard-list")
    public String dashboardList() {
        return "dashboard-list";
    }
   
} 