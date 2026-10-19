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
    public String verifyPayment(
            @RequestParam("paymentId") int paymentId,
            @RequestParam("action") String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) return "redirect:/login";

        int adminId = Integer.parseInt(session.getAttribute("user_id").toString());
        boolean success = paymentRefundService.processPaymentVerification(paymentId, action, adminId);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Payment status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update payment status.");
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

        int adminId = Integer.parseInt(session.getAttribute("user_id").toString());
        boolean success = paymentRefundService.updateRefundStatus(refundId, status, adminId);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Refund request status updated!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update refund status.");
        }

        return "redirect:/admin/payments-refunds?activeTab=refund";
    }
}