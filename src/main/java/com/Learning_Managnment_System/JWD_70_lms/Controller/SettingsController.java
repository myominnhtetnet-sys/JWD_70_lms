package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.SettingsService;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountRuleTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.PaymentMethodBean;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/admin/settings", "/admin/Settings"}) // 🟢 စာလုံးအသေးရော အကြီးရော နှစ်မျိုးလုံး လက်ခံမည်
public class SettingsController {

    @Autowired
    private SettingsService settingsService;
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


    // Load Settings Page
    @GetMapping
    public String viewSettings(Model model) {
        model.addAttribute("paymentMethods", settingsService.getAllPaymentMethods());
        model.addAttribute("discountTypes", settingsService.getAllDiscountTypes());
        model.addAttribute("discountRuleTypes", settingsService.getAllDiscountRuleTypes());
        return "Settings"; // templates ဖိုင်တွဲထဲရှိ Settings.html
    }

    // ================= PAYMENT METHOD ENDPOINTS =================
    @PostMapping("/payment/save")
    public String savePaymentMethod(@ModelAttribute PaymentMethodBean paymentMethod, RedirectAttributes redirectAttributes) {
        settingsService.savePaymentMethod(paymentMethod);
        redirectAttributes.addFlashAttribute("successMessage", "Payment method saved successfully!");
        return "redirect:/admin/settings";
    }

    @GetMapping("/payment/delete/{id}")
    public String deletePaymentMethod(@PathVariable int id, RedirectAttributes redirectAttributes) {
        settingsService.deletePaymentMethod(id);
        redirectAttributes.addFlashAttribute("successMessage", "Payment method deleted successfully!");
        return "redirect:/admin/settings";
    }

    // ================= DISCOUNT TYPE ENDPOINTS =================
    @PostMapping("/discount-type/save")
    public String saveDiscountType(@ModelAttribute DiscountTypeBean discountType, RedirectAttributes redirectAttributes) {
        settingsService.saveDiscountType(discountType);
        redirectAttributes.addFlashAttribute("successMessage", "Discount type saved successfully!");
        return "redirect:/admin/settings";
    }

    @GetMapping("/discount-type/delete/{typeCode}")
    public String deleteDiscountType(@PathVariable String typeCode, RedirectAttributes redirectAttributes) {
        settingsService.deleteDiscountType(typeCode);
        redirectAttributes.addFlashAttribute("successMessage", "Discount type deleted successfully!");
        return "redirect:/admin/settings";
    }

    // ================= DISCOUNT RULE TYPE ENDPOINTS =================
    @PostMapping("/discount-rule/save")
    public String saveDiscountRuleType(@ModelAttribute DiscountRuleTypeBean discountRuleType, RedirectAttributes redirectAttributes) {
        settingsService.saveDiscountRuleType(discountRuleType);
        redirectAttributes.addFlashAttribute("successMessage", "Discount rule type saved successfully!");
        return "redirect:/admin/settings";
    }

    @GetMapping("/discount-rule/delete/{ruleTypeCode}")
    public String deleteDiscountRuleType(@PathVariable String ruleTypeCode, RedirectAttributes redirectAttributes) {
        settingsService.deleteDiscountRuleType(ruleTypeCode);
        redirectAttributes.addFlashAttribute("successMessage", "Discount rule type deleted successfully!");
        return "redirect:/admin/settings";
    }
}