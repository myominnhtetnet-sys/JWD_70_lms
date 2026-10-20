package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.PaymentRefundService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/payments-refunds")
public class PaymentRefundController {

    @Autowired
    private PaymentRefundService paymentRefundService;

    private boolean checkAdmin(HttpSession session) {
        Object role = session.getAttribute("role_id");
        return role != null && "1".equals(role.toString());
    }

    @GetMapping
    public String index(
            @RequestParam(required = false, defaultValue = "") String payKeyword,
            @RequestParam(required = false, defaultValue = "ALL") String payStatus,
            @RequestParam(required = false, defaultValue = "") String refKeyword,
            @RequestParam(required = false, defaultValue = "ALL") String refStatus,
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        // Stats Summary Cards
        model.addAttribute("totalPayments", paymentRefundService.countTotalPayments());
        model.addAttribute("pendingPayments", paymentRefundService.countPendingPayments());
        model.addAttribute("verifiedPayments", paymentRefundService.countVerifiedPayments());
        model.addAttribute("pendingRefunds", paymentRefundService.countPendingRefunds());

        // Tables
        model.addAttribute("payments", paymentRefundService.getPayments(payKeyword, payStatus));
        model.addAttribute("refunds", paymentRefundService.getRefunds(refKeyword, refStatus));

        model.addAttribute("payKeyword", payKeyword);
        model.addAttribute("selectedPayStatus", payStatus);
        model.addAttribute("refKeyword", refKeyword);
        model.addAttribute("selectedRefStatus", refStatus);

        return "payments-refunds";
    }

    @PostMapping("/verify-payment")
    public String verifyPayment(@RequestParam("paymentId") Long paymentId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        try {
            // "adminId" အစား Session ထဲမှာ ရှိတဲ့ "user_id" ကို ယူခြင်း
            Object userObj = session.getAttribute("user_id");
            
            // အကယ်၍ user_id မရှိသေးလျှင် (Testing အတွက် Default 1L ထားမည် သို့မဟုတ် ယူမည်)
            Long adminId = (userObj != null) ? Long.valueOf(userObj.toString()) : 1L;

            // ငွေအတည်ပြုသည့် Service ခေါ်ယူခြင်း
            paymentRefundService.verifyPayment(paymentId, adminId);
            
            redirectAttributes.addFlashAttribute("success", "Payment verified successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error verifying payment: " + e.getMessage());
        }
        return "redirect:/admin/payments-refunds";
    }
    
 // Verify နဲ့ Reject နှစ်ခုစလုံးကို တစ်နေရာတည်းမှ ခွဲထုတ်လက်ခံမည့် Method
    @PostMapping("/process-payment")
    public String processPayment(
            @RequestParam("paymentId") Long paymentId,
            @RequestParam("action") String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Object userObj = session.getAttribute("user_id");
            Long adminId = (userObj != null) ? Long.valueOf(userObj.toString()) : 1L;
            if ("verify".equalsIgnoreCase(action)) {
                paymentRefundService.verifyPayment(paymentId, adminId);
                redirectAttributes.addFlashAttribute("success", "Payment verified as SUCCESS!");
            } else if ("reject".equalsIgnoreCase(action)) {
                paymentRefundService.rejectPayment(paymentId, adminId);
                redirectAttributes.addFlashAttribute("success", "Payment status reset to PENDING!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing payment: " + e.getMessage());
        }
        
        return "redirect:/admin/payments-refunds";
    }

    @PostMapping("/update-refund-status")
    public String updateRefundStatus(
            @RequestParam("refundId") int refundId,
            @RequestParam("status") String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) return "redirect:/login";

        Object userObj = session.getAttribute("user_id");
        int adminId = (userObj != null) ? Integer.parseInt(userObj.toString()) : 1;

        boolean success = paymentRefundService.updateRefundStatus(refundId, status, adminId);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Refund request status updated!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update refund status.");
        }

        return "redirect:/admin/payments-refunds?activeTab=refund";
    }
    
    @PostMapping("/reject-payment")
    public String rejectPayment(@RequestParam("paymentId") Long paymentId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Object userObj = session.getAttribute("user_id");
            Long adminId = (userObj != null) ? Long.valueOf(userObj.toString()) : 1L;

            // ငွေငြင်းပယ်သည့် Service ကို ခေါ်သုံးခြင်း (Status ကို REJECTED သို့မဟုတ် FAILED သို့ပြောင်းရန်)
            paymentRefundService.rejectPayment(paymentId, adminId);
            
            redirectAttributes.addFlashAttribute("success", "Payment rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error rejecting payment: " + e.getMessage());
        }
        return "redirect:/admin/payments-refunds";
    }
}