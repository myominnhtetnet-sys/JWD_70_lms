package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleRespority;
import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleItemRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRule;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRuleItem;
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
@RequestMapping("/admin/installments")
public class InstallmentRuleController {

    @Autowired
    private InstallmentRuleRespority installmentRuleRepository;

    @Autowired
    private InstallmentRuleItemRespority installmentRuleItemRepository;
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


    @GetMapping({"", "/", "/list"})
    public String listInstallmentRules(Model model) {
        List<AdminBeanInstallmentRule> rules = installmentRuleRepository.getAllInstallmentRules();
        model.addAttribute("installmentRules", rules);
        return "InstallmentRule-list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        AdminBeanInstallmentRule rule = new AdminBeanInstallmentRule();
        rule.setIsActive("1");
        model.addAttribute("installmentRule", rule);
        return "InstallmentRule-form";
    }
    
    @PostMapping("/save")
    public String saveInstallmentRule(
            @ModelAttribute("installmentRule") AdminBeanInstallmentRule rule,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            boolean isNew = rule.getRuleId() <= 0;

            if (isNew) {
                AdminBeanInstallmentRule created = installmentRuleRepository.createInstallmentRule(rule);
                redirectAttributes.addFlashAttribute("successMessage", "Installment rule created successfully.");
            } else {
                installmentRuleRepository.updateInstallmentRule(rule.getRuleId(), rule);
                redirectAttributes.addFlashAttribute("successMessage", "Installment rule updated successfully.");
            }

            return "redirect:/admin/installments/list";

        } catch (DuplicateKeyException e) {
            model.addAttribute("errorMessage", "The installment rule name or details already exist.");
            return "InstallmentRule-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model) {

        AdminBeanInstallmentRule rule = installmentRuleRepository.getInstallmentRuleById(id);

        if (rule == null) {
            return "redirect:/admin/installments/list";
        }

        model.addAttribute("installmentRule", rule);
        model.addAttribute("ruleItems", installmentRuleItemRepository.getItemsByRuleId(id));

        return "InstallmentRule-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteInstallmentRule(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        installmentRuleItemRepository.deleteInstallmentRuleItem(id);
        installmentRuleRepository.deleteInstallmentRule(id);

        redirectAttributes.addFlashAttribute("successMessage", "Installment rule deleted successfully.");

        return "redirect:/admin/installments/list";
    }

    @PostMapping("/items/save")
    public String saveInstallmentRuleItem(
            @ModelAttribute("ruleItem") AdminBeanInstallmentRuleItem item,
            RedirectAttributes redirectAttributes) {

        if (item.getRuleItemId() <= 0) {
            installmentRuleItemRepository.createInstallmentRuleItem(item);
            redirectAttributes.addFlashAttribute("successMessage", "Installment rule item added successfully.");
        } else {
            installmentRuleItemRepository.updateInstallmentRuleItem(item.getRuleItemId(), item);
            redirectAttributes.addFlashAttribute("successMessage", "Installment rule item updated successfully.");
        }

        return "redirect:/admin/installments/edit/" + item.getRuleId();
    }

    @GetMapping("/items/delete/{id}")
    public String deleteInstallmentRuleItem(
            @PathVariable("id") Integer id,
            @RequestParam("ruleId") Integer ruleId,
            RedirectAttributes redirectAttributes) {

        installmentRuleItemRepository.deleteInstallmentRuleItem(id);
        redirectAttributes.addFlashAttribute("successMessage", "Installment rule item deleted successfully.");

        return "redirect:/admin/installments/edit/" + ruleId;
    }
}