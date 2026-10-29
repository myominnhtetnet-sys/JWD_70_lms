
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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

    @PostMapping("/student/process-payment")
    public String processPayment(
            @RequestParam(value = "paymentMethodId", defaultValue = "0")
            int paymentMethodId,

            @RequestParam("amount")
            Double amount,

            @RequestParam(value = "paymentType",
                    defaultValue = "Full Course Fee")
            String paymentType,

            @RequestParam(value = "note", required = false)
            String note,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        Object studentIdObj = session.getAttribute("studentId");

        if (email == null) {
            return "redirect:/login";
        }

        if (paymentMethodId <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please select a payment method."
            );
            return "redirect:/student/checkout";
        }

        if (amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please enter a valid payment amount."
            );
            return "redirect:/student/checkout";
        }

        // Session ထဲက studentId မရှိရင် payment မလုပ်စေပါ
        if (studentIdObj == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Student session expired. Please log in again."
            );
            return "redirect:/login";
        }

        int userId;

        try {
            userId = Integer.parseInt(studentIdObj.toString());
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid student session. Please log in again."
            );
            return "redirect:/login";
        }

        Integer enrollmentId =
                paymentRepository.getEnrollmentIdByUserId(userId);

        if (enrollmentId == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No enrollment found for this student."
            );
            return "redirect:/student/checkout";
        }

        String transactionNo =
                "TXN-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        PaymentBean payment = new PaymentBean();

        payment.setEnrollmentId(enrollmentId);
        payment.setScheduleId(1);
        payment.setPaymentMethodId(paymentMethodId);
        payment.setAmount(amount);
        payment.setTransactionNo(transactionNo);
        payment.setPaymentType(paymentType);
        payment.setNote(note);
        payment.setProofImage(null);

        // Payment တကယ်အတည်ပြုပြီးမှ SUCCESS သတ်မှတ်သင့်ပါတယ်
        payment.setStatus("PENDING");

        int result = paymentRepository.savePayment(payment);

        if (result > 0) {
            return "redirect:/student/payment-success"
                    + "?amount=" + amount.toString()
                    + "&transactionNo=" + transactionNo
                    + "&paymentType=" + URLEncoder.encode(
                            paymentType, StandardCharsets.UTF_8)
                    + "&note=" + URLEncoder.encode(
                            note != null ? note : "",
                            StandardCharsets.UTF_8);
        }

        redirectAttributes.addFlashAttribute(
                "error",
                "Payment submission failed. Please try again."
        );

        return "redirect:/student/checkout";
    }

    @GetMapping("/student/payment-success")
    public String showSuccessPage(
            @RequestParam(value = "amount", required = false)
            BigDecimal amount,

            @RequestParam(value = "transactionNo", required = false)
            String transactionNo,

            @RequestParam(value = "paymentType", required = false)
            String paymentType,

            @RequestParam(value = "note", required = false)
            String note,

            HttpSession session,
            Model model) {

        if (session.getAttribute("email") == null) {
            return "redirect:/login";
        }

        model.addAttribute("amount", amount);
        model.addAttribute("transactionNo", transactionNo);
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("note", note);

        return "payment-success";
    }
}
