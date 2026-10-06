package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.PaymentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.PaymentBean;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/student/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> paymentMethods = paymentRepository.getActivePaymentMethods();
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("fullName", session.getAttribute("fullName"));

        return "checkout";
    }

    @PostMapping("/student/process-payment")
    public String processPayment(@RequestParam(value = "paymentMethodId", defaultValue = "0") int paymentMethodId,
                                 @RequestParam("amount") double amount,
                                 @RequestParam(value = "transactionNo", required = false) String transactionNo,
                                 @RequestParam(value = "paymentType", defaultValue = "Full Course Fee") String paymentType,
                                 @RequestParam(value = "note", required = false) String note,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        Object studentIdObj = session.getAttribute("studentId");

        if (email == null) {
            return "redirect:/login";
        }

        if (paymentMethodId == 0) {
            redirectAttributes.addFlashAttribute("error", "ကျေးဇူးပြု၍ Payment Method တစ်ခုခုကို ရွေးချယ်ပေးပါရန်။");
            return "redirect:/student/checkout";
        }

        int userId = (studentIdObj != null) ? Integer.parseInt(studentIdObj.toString()) : 1;
        
      
        Integer validEnrollmentId = paymentRepository.getEnrollmentIdByUserId(userId);

        PaymentBean payment = new PaymentBean();
        payment.setEnrollmentId(validEnrollmentId); 
        payment.setScheduleId(1);
        payment.setPaymentMethodId(paymentMethodId);
        payment.setAmount(amount);
        payment.setTransactionNo((transactionNo != null && !transactionNo.trim().isEmpty()) ? transactionNo : "N/A");
        payment.setPaymentType(paymentType);
        payment.setNote(note);
        payment.setProofImage(null);
        payment.setStatus("SUCCESS");

        int result = paymentRepository.savePayment(payment);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("amount", amount);
            redirectAttributes.addFlashAttribute("transactionNo", payment.getTransactionNo());
            redirectAttributes.addFlashAttribute("paymentType", paymentType);
            redirectAttributes.addFlashAttribute("note", note);
            return "redirect:/student/payment-success";
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment submission failed. Please try again.");
            return "redirect:/student/checkout";
        }
    }

    @GetMapping("/student/payment-success")
    public String showSuccessPage(HttpSession session) {
        if (session.getAttribute("email") == null) {
            return "redirect:/login";
        }
        return "payment-success";
    }
}