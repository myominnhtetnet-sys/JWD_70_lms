package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.PaymentRefundService;

@Controller
@RequestMapping("/admin/overdue-installments")
public class OverdueInstallmentController {

    @Autowired
    private PaymentRefundService paymentRefundService;

    @GetMapping
    public String index(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            Model model) {

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