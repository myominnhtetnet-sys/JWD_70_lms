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

@Controller
@RequestMapping("/admin/payments-refunds")
public class PaymentRefundController {

    @Autowired
    private PaymentRefundService paymentRefundService;

    @GetMapping
    public String index(
            @RequestParam(required = false, defaultValue = "") String payKeyword,
            @RequestParam(required = false, defaultValue = "ALL") String payStatus,
            @RequestParam(required = false, defaultValue = "") String refKeyword,
            @RequestParam(required = false, defaultValue = "ALL") String refStatus,
            Model model) {

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
    public String verifyPayment(@RequestParam("paymentId") Long paymentId, RedirectAttributes redirectAttributes) {
        try {
            Long adminId = 1L; // Default Admin ID
            paymentRefundService.verifyPayment(paymentId, adminId);
            redirectAttributes.addFlashAttribute("success", "Payment verified successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error verifying payment: " + e.getMessage());
        }
        return "redirect:/admin/payments-refunds";
    }
    
    @PostMapping("/process-payment")
    public String processPayment(
            @RequestParam("paymentId") Long paymentId,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        try {
            Long adminId = 1L; // Default Admin ID
            
            if ("verify".equalsIgnoreCase(action)) {
                paymentRefundService.verifyPayment(paymentId, adminId);
                redirectAttributes.addFlashAttribute("success", "Payment verified as SUCCESS!");
            } else if ("reject".equalsIgnoreCase(action)) {
                paymentRefundService.rejectPayment(paymentId, adminId);
                redirectAttributes.addFlashAttribute("success", "Payment status updated to REJECTED!");
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
            RedirectAttributes redirectAttributes) {

        int adminId = 1; // Default Admin ID
        boolean success = paymentRefundService.updateRefundStatus(refundId, status, adminId);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Refund request status updated!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update refund status.");
        }

        return "redirect:/admin/payments-refunds?activeTab=refund";
    }
    
    @PostMapping("/reject-payment")
    public String rejectPayment(@RequestParam("paymentId") Long paymentId, RedirectAttributes redirectAttributes) {
        try {
            Long adminId = 1L; // Default Admin ID
            paymentRefundService.rejectPayment(paymentId, adminId);
            redirectAttributes.addFlashAttribute("success", "Payment rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error rejecting payment: " + e.getMessage());
        }
        return "redirect:/admin/payments-refunds";
    }
}