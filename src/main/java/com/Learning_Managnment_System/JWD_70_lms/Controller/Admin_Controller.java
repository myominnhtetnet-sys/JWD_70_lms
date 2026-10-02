package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

<<<<<<< Updated upstream
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
=======
    private static final Logger log =
            LoggerFactory.getLogger(Admin_Controller.class);

    private final Admin_Repository adminRepository;

    public Admin_Controller(Admin_Repository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard() {
        return "admin";
    }

    // =========================================================
    // CATEGORY LIST
    // Supports:
    // /admin/category
    // /admin/categories
    // =========================================================

    @GetMapping({"/category", "/categories"})
    public String categoryIndex() {
        return "redirect:/admin/category/list";
    }

    // =========================================================
    // CATEGORY LIST PAGE
    // =========================================================

    @GetMapping({"/category/list", "/categories/list"})
    public String getAllCategories(
            @ModelAttribute("editedId") String editedIdStr,
            @ModelAttribute("createdId") String createdIdStr,
            Model model) {

        List<AdminBean_category> categoryList =
                adminRepository.getAllCategories();

        if (categoryList != null && !categoryList.isEmpty()) {

            // -------------------------------------------------
            // EDITED BADGE
            // -------------------------------------------------

            if (editedIdStr != null && !editedIdStr.isEmpty()) {

                try {

                    int editedId = Integer.parseInt(editedIdStr);

                    categoryList.stream()
                            .filter(c ->
                                    c.getCategory_id() != null
                                    && c.getCategory_id() == editedId)
                            .findFirst()
                            .ifPresent(c -> {
                                c.set_edited(true);
                                c.set_new(false);
                            });

                } catch (NumberFormatException e) {

                    log.warn(
                            "Invalid editedId flash attribute: {}",
                            editedIdStr
                    );
                }
            }

            // -------------------------------------------------
            // NEW BADGE
            // -------------------------------------------------

            if (createdIdStr != null && !createdIdStr.isEmpty()) {

                try {

                    int createdId = Integer.parseInt(createdIdStr);

                    categoryList.stream()
                            .filter(c ->
                                    c.getCategory_id() != null
                                    && c.getCategory_id() == createdId)
                            .findFirst()
                            .ifPresent(c -> {
                                c.set_new(true);
                                c.set_edited(false);
                            });

                } catch (NumberFormatException e) {

                    log.warn(
                            "Invalid createdId flash attribute: {}",
                            createdIdStr
                    );
                }
            }

            model.addAttribute(
                    "categoryResults",
                    categoryList
            );

        } else {

            model.addAttribute(
                    "notFoundMessage",
                    "No category records found."
            );

            model.addAttribute(
                    "categoryResults",
                    List.of()
            );
        }

        return "category-list";
    }

    // =========================================================
    // CREATE CATEGORY FORM
    // =========================================================

    @GetMapping({"/category/create", "/categories/create"})
    public String showCreateForm(Model model) {

        AdminBean_category category =
                new AdminBean_category();

        // Default active
        category.setIs_active(1);

        model.addAttribute(
                "category",
                category
        );

        model.addAttribute(
                "parentCategories",
                adminRepository.getAllCategories()
        );

        return "category-form";
    }

    // =========================================================
    // EDIT CATEGORY FORM
    // =========================================================

    @GetMapping({
            "/category/edit/{id}",
            "/categories/edit/{id}"
    })
    public String showEditForm(
            @PathVariable("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        AdminBean_category category =
                adminRepository.getCategoryById(id);

        if (category == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Category record not found."
            );

            return "redirect:/admin/category/list";
        }

        model.addAttribute(
                "category",
                category
        );

        model.addAttribute(
                "parentCategories",
                adminRepository.getAllCategories()
        );

        return "category-form";
    }

    // =========================================================
    // SAVE / UPDATE CATEGORY
    // =========================================================

    @PostMapping({
            "/category/save",
            "/categories/save"
    })
    public String saveCategory(
            @ModelAttribute("category")
            AdminBean_category category,
            RedirectAttributes redirectAttributes) {

        // Default active
        if (category.getIs_active() == null) {
            category.setIs_active(1);
        }

        // -------------------------------------------------
        // UPDATE
        // -------------------------------------------------

        if (category.getCategory_id() != null
                && category.getCategory_id() > 0) {

            int rowsAffected =
                    adminRepository.updateCategory(category);

            if (rowsAffected > 0) {

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Category updated successfully."
                );

                redirectAttributes.addFlashAttribute(
                        "editedId",
                        String.valueOf(
                                category.getCategory_id()
                        )
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Failed to update category."
                );
            }

        }

        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------

        else {

            int rowsAffected =
                    adminRepository.saveCategory(category);

            if (rowsAffected > 0) {

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Category created successfully."
                );

                /*
                 * This works only if saveCategory()
                 * sets the generated category_id
                 * inside the category object.
                 */
                if (category.getCategory_id() != null) {

                    redirectAttributes.addFlashAttribute(
                            "createdId",
                            String.valueOf(
                                    category.getCategory_id()
                            )
                    );
                }

            } else {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Failed to create category."
                );
            }
        }

        return "redirect:/admin/category/list";
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    @GetMapping({
            "/category/delete/{id}",
            "/categories/delete/{id}"
    })
    public String deleteCategory(
            @PathVariable("id") int id,
            RedirectAttributes redirectAttributes) {

        int rowsAffected =
                adminRepository.deleteCategory(id);

        if (rowsAffected > 0) {

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Category deleted successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to delete category."
            );
        }

        return "redirect:/admin/category/list";
>>>>>>> Stashed changes
    }
}