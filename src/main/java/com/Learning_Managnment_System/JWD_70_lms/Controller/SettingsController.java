package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.SettingsService;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountRuleTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.PaymentMethodBean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/Settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    // Load Settings Page
    @GetMapping
    public String viewSettings(Model model) {
        model.addAttribute("paymentMethods", settingsService.getAllPaymentMethods());
        model.addAttribute("discountTypes", settingsService.getAllDiscountTypes());
        model.addAttribute("discountRuleTypes", settingsService.getAllDiscountRuleTypes());
        return "Settings"; // Renders src/main/resources/templates/Settings.html
    }

    // ================= PAYMENT METHOD ENDPOINTS =================
    @PostMapping("/payment/save")
    public String savePaymentMethod(@ModelAttribute PaymentMethodBean paymentMethod, RedirectAttributes redirectAttributes) {
        settingsService.savePaymentMethod(paymentMethod);
        redirectAttributes.addFlashAttribute("successMessage", "Payment method saved successfully!");
        return "redirect:/admin/Settings";
    }

    @GetMapping("/payment/delete/{id}")
    public String deletePaymentMethod(@PathVariable int id, RedirectAttributes redirectAttributes) {
        settingsService.deletePaymentMethod(id);
        redirectAttributes.addFlashAttribute("successMessage", "Payment method deleted successfully!");
        return "redirect:/admin/Settings";
    }

    // ================= DISCOUNT TYPE ENDPOINTS =================
    @PostMapping("/discount-type/save")
    public String saveDiscountType(@ModelAttribute DiscountTypeBean discountType, RedirectAttributes redirectAttributes) {
        settingsService.saveDiscountType(discountType);
        redirectAttributes.addFlashAttribute("successMessage", "Discount type saved successfully!");
        return "redirect:/admin/Settings";
    }

    @GetMapping("/discount-type/delete/{typeCode}")
    public String deleteDiscountType(@PathVariable String typeCode, RedirectAttributes redirectAttributes) {
        settingsService.deleteDiscountType(typeCode);
        redirectAttributes.addFlashAttribute("successMessage", "Discount type deleted successfully!");
        return "redirect:/admin/Settings";
    }

    // ================= DISCOUNT RULE TYPE ENDPOINTS =================
    @PostMapping("/discount-rule/save")
    public String saveDiscountRuleType(@ModelAttribute DiscountRuleTypeBean discountRuleType, RedirectAttributes redirectAttributes) {
        settingsService.saveDiscountRuleType(discountRuleType);
        redirectAttributes.addFlashAttribute("successMessage", "Discount rule type saved successfully!");
        return "redirect:/admin/Settings";
    }

    @GetMapping("/discount-rule/delete/{ruleTypeCode}")
    public String deleteDiscountRuleType(@PathVariable String ruleTypeCode, RedirectAttributes redirectAttributes) {
        settingsService.deleteDiscountRuleType(ruleTypeCode);
        redirectAttributes.addFlashAttribute("successMessage", "Discount rule type deleted successfully!");
        return "redirect:/admin/Settings";
    }
}