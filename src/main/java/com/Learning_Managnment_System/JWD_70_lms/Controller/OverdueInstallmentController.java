package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.PaymentRefundService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/overdue-installments")
public class OverdueInstallmentController {

    @Autowired
    private PaymentRefundService paymentRefundService;

    private boolean checkAdmin(HttpSession session) {
        Object role = session.getAttribute("role_id");
        return role != null && "1".equals(role.toString());
    }

    @GetMapping
    public String index(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) return "redirect:/login";

        model.addAttribute("overdueList", paymentRefundService.getOverdueInstallments(keyword, status));
        model.addAttribute("totalOverdue", paymentRefundService.countOverdueInstallments());
        model.addAttribute("totalCritical", paymentRefundService.countCriticalInstallments());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);

        return "overdue-installments";
    }

    @PostMapping("/send-reminder")
    public String sendReminder(
            @RequestParam("scheduleId") int scheduleId,
            RedirectAttributes redirectAttributes) {

        boolean success = paymentRefundService.updateReminderSentAt(scheduleId);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Payment reminder sent and logged successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to send reminder.");
        }
        return "redirect:/admin/overdue-installments";
    }
}