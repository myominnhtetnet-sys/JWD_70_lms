package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleRespority;
import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleItemRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRule;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRuleItem;
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

    // =========================================
    // INSTALLMENT RULE LIST
    // URL: /admin/installments, /admin/installments/list
    // =========================================
    @GetMapping({"", "/", "/list"})
    public String listInstallmentRules(Model model) {
        List<AdminBeanInstallmentRule> rules = installmentRuleRepository.getAllInstallmentRules();
        model.addAttribute("installmentRules", rules);
        return "InstallmentRule-list";
    }

    // =========================================
    // ADD INSTALLMENT RULE FORM
    // URL: /admin/installments/new
    // =========================================
    @GetMapping("/new")
    public String showAddForm(Model model) {
        AdminBeanInstallmentRule rule = new AdminBeanInstallmentRule();
        rule.setIsActive("1");
        model.addAttribute("installmentRule", rule);
        return "InstallmentRule-form";
    }

    // =========================================
    // SAVE OR UPDATE INSTALLMENT RULE
    // URL: POST /admin/installments/save
    // =========================================
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

    // =========================================
    // EDIT INSTALLMENT RULE
    // URL: /admin/installments/edit/{id}
    // =========================================
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

    // =========================================
    // DELETE INSTALLMENT RULE
    // URL: /admin/installments/delete/{id}
    // =========================================
    @GetMapping("/delete/{id}")
    public String deleteInstallmentRule(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        installmentRuleItemRepository.deleteInstallmentRuleItem(id);
        installmentRuleRepository.deleteInstallmentRule(id);

        redirectAttributes.addFlashAttribute("successMessage", "Installment rule deleted successfully.");

        return "redirect:/admin/installments/list";
    }

    // =========================================
    // SAVE RULE ITEM (STEP BREAKDOWN)
    // URL: POST /admin/installments/items/save
    // =========================================
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

    // =========================================
    // DELETE RULE ITEM (STEP BREAKDOWN)
    // URL: /admin/installments/items/delete/{id}?ruleId={ruleId}
    // =========================================
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