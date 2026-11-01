package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CategoryRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;
	 private boolean isNotAdmin(HttpSession session) {
	        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
	        Object roleIdObj = session.getAttribute("roleId");
	        
	        int roleId = 0;
	        if (currentUser != null) {
	            roleId = currentUser.getRole_id();
	        } else if (roleIdObj != null) {
	            roleId = (int) roleIdObj;
	        }
	        
	        return roleId != 1; 
	    }
	    
	    @ModelAttribute
	    public void addCommonAttributes(HttpSession session, Model model) {
	        String displayName = "Administrator";
	        int roleId = 1;

	        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
	        if (currentUser != null) {
	            if (currentUser.getFull_name() != null) {
	                displayName = currentUser.getFull_name();
	            }
	            roleId = currentUser.getRole_id();
	        } 
	        
	        else {
	            if (session.getAttribute("fullName") != null) {
	                displayName = (String) session.getAttribute("fullName");
	            }
	            if (session.getAttribute("roleId") != null) {
	                roleId = (int) session.getAttribute("roleId");
	            }
	        }

	        // Database မှ role_id ကို အခြေခံ၍ Role Name ဖော်ထုတ်ခြင်း
	        String roleName = "User";
	        if (roleId == 1) {
	            roleName = "Administrator";
	        } else if (roleId == 2) {
	            roleName = "Instructor";
	        } else if (roleId == 3) {
	            roleName = "Student";
	        }

	        model.addAttribute("fullName", displayName);
	        model.addAttribute("loggedInRole", roleName);
	    }


	// 🟢 Category List မျက်နှာပြင်
	@GetMapping({ "/categories", "/categories/list" })
	public String listCategories(Model model) {
		List<AdminBean_category> categories = categoryRepository.getAllCategories();
		model.addAttribute("categories", categories);
		return "category-list"; 
	}

	// 🟢 NEW CATEGORY FORM သို့ မှန်ကန်စွာ သွားစေရန် (Fixed)
	@GetMapping("/categories/new")
	public String showCreateForm(Model model) {
		// Form ထဲတွင် th:object="${category}" သုံးထားသောကြောင့် Object အသစ် ပို့ပေးရပါမည်
		model.addAttribute("category", new AdminBean_category());
		return "category-form"; // templates ဖိုင်တွဲထဲရှိ category-form.html ကို ခေါ်မည်
	}

	@PostMapping("/categories/save")
	public String saveCategory(@ModelAttribute("category") AdminBean_category category,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			boolean isNew = category.getCategory_id() == null || category.getCategory_id() <= 0;

			if (isNew) {
				int newId = categoryRepository.saveCategory(category);
				redirectAttributes.addFlashAttribute("activeId", newId);
				redirectAttributes.addFlashAttribute("actionType", "NEW");
				redirectAttributes.addFlashAttribute("successMessage", "Category created successfully.");
			} else {
				categoryRepository.updateCategory(category);
				redirectAttributes.addFlashAttribute("activeId", category.getCategory_id());
				redirectAttributes.addFlashAttribute("actionType", "EDITED");
				redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
			}
			return "redirect:/admin/categories/list";

		} catch (DuplicateKeyException e) {
			model.addAttribute("errorMessage", "The category name or slug already exists.");
			model.addAttribute("parentCategories", categoryRepository.getAllCategories());
			return "category-form";
		}
	}

	@GetMapping("/categories/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model) {
		AdminBean_category category = categoryRepository.getCategoryById(id);
		if (category == null) {
			return "redirect:/admin/categories/list";
		}
		model.addAttribute("category", category);
		model.addAttribute("parentCategories", categoryRepository.getAllCategories());
		return "category-form";
	}

	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		categoryRepository.deleteCategory(id);
		redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
		return "redirect:/admin/categories/list";
	}
}