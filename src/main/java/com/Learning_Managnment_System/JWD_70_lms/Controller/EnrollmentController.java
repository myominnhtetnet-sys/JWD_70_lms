package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.EnrollmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentDetailBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    private boolean checkAdmin(HttpSession session) {

        Object role = session.getAttribute("role_id");

        if (role == null) {
            return false;
        }

        try {
            return Integer.parseInt(role.toString()) == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
    @GetMapping("/detail/{id}")
    public String viewEnrollmentDetail(@PathVariable int id, HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        EnrollmentDetailBean detail = enrollmentRepo.getEnrollmentById(id);
        if (detail != null) {
            detail.setInstallments(enrollmentRepo.getInstallmentsByEnrollmentId(id));
            detail.setPayments(enrollmentRepo.getPaymentsByEnrollmentId(id));
        }

        model.addAttribute("detail", detail);
        model.addAttribute("fullName", session.getAttribute("fullName"));
        return "enrollment-detail"; // enrollment-detail.html သို့ ပို့မည်
    }
    // =====================================================
    // ENROLLMENT LIST
    // GET /admin/enrollments
    // =====================================================

    @GetMapping
    public String listEnrollments(
            @RequestParam(required = false, defaultValue = "")
            String keyword,

            @RequestParam(required = false, defaultValue = "ALL")
            String status,

            @RequestParam(required = false, defaultValue = "ALL")
            String paymentStatus,

            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "enrollments",
                enrollmentRepo.searchEnrollments(
                        keyword,
                        status,
                        paymentStatus
                )
        );

        model.addAttribute(
                "students",
                enrollmentRepo.getStudents()
        );

        model.addAttribute(
                "batches",
                enrollmentRepo.getBatches()
        );

        model.addAttribute(
                "totalEnrollments",
                enrollmentRepo.countEnrollments()
        );

        model.addAttribute(
                "activeEnrollments",
                enrollmentRepo.countActiveEnrollments()
        );

        model.addAttribute(
                "pendingEnrollments",
                enrollmentRepo.countPendingEnrollments()
        );

        model.addAttribute(
                "totalAmount",
                enrollmentRepo.totalEnrollmentAmount()
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPaymentStatus", paymentStatus);

        model.addAttribute(
                "enrollment",
                new EnrollmentBean()
        );

        model.addAttribute(
                "enrollmentStatuses",
                Arrays.asList(
                        "PENDING",
                        "ACTIVE",
                        "COMPLETED",
                        "DROPPED",
                        "CANCELLED"
                )
        );

        model.addAttribute(
                "paymentStatuses",
                Arrays.asList(
                        "UNPAID",
                        "PARTIAL",
                        "PAID",
                        "REFUNDED"
                )
        );

        model.addAttribute(
                "paymentTypes",
                Arrays.asList(
                        "FULL",
                        "INSTALLMENT",
                        "FREE"
                )
        );

        model.addAttribute(
                "fullName",
                session.getAttribute("fullName")
        );

        return "enrollment";
    }

    // =====================================================
    // CREATE ENROLLMENT
    // POST /admin/enrollments/create
    // =====================================================

    @PostMapping("/create")
    public String createEnrollment(
            @ModelAttribute("enrollment")
            EnrollmentBean enrollment,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (enrollment.getUser_id() <= 0
                || enrollment.getBatch_id() <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please select a student and batch."
            );

            return "redirect:/admin/enrollments";
        }

        if (enrollment.getOriginal_amount() == null
                || enrollment.getDiscount_amount() == null
                || enrollment.getScholarship_amount() == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please enter valid enrollment amounts."
            );

            return "redirect:/admin/enrollments";
        }

        if (enrollment.getOriginal_amount().signum() < 0
                || enrollment.getDiscount_amount().signum() < 0
                || enrollment.getScholarship_amount().signum() < 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Amounts cannot be negative."
            );

            return "redirect:/admin/enrollments";
        }

        if (enrollment.getDiscount_amount()
                .add(enrollment.getScholarship_amount())
                .compareTo(enrollment.getOriginal_amount()) > 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Discount and scholarship cannot exceed the original amount."
            );

            return "redirect:/admin/enrollments";
        }

        BigDecimal finalAmount =
                enrollment.getOriginal_amount()
                        .subtract(enrollment.getDiscount_amount())
                        .subtract(enrollment.getScholarship_amount());

        enrollment.setFinal_amount(finalAmount);

        if (!isValidPaymentType(enrollment.getPayment_type())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid payment type."
            );

            return "redirect:/admin/enrollments";
        }

        if (!isValidPaymentStatus(enrollment.getPayment_status())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid payment status."
            );

            return "redirect:/admin/enrollments";
        }

        if (!isValidEnrollmentStatus(enrollment.getStatus())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid enrollment status."
            );

            return "redirect:/admin/enrollments";
        }

        try {

            int result =
                    enrollmentRepo.createEnrollment(enrollment);

            if (result > 0) {

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Enrollment created successfully."
                );

            } else {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Unable to create enrollment."
                );
            }

        } catch (DataAccessException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Database error while creating enrollment. Please check your table columns."
            );
        }

        return "redirect:/admin/enrollments";
    }

    // =====================================================
    // CHANGE ENROLLMENT STATUS
    // POST /admin/enrollments/status
    // =====================================================

    @PostMapping("/status")
    public String changeEnrollmentStatus(
            @RequestParam int enrollmentId,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (enrollmentId <= 0
                || !isValidEnrollmentStatus(status)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid enrollment status."
            );

            return "redirect:/admin/enrollments";
        }

        int result =
                enrollmentRepo.updateEnrollmentStatus(
                        enrollmentId,
                        status
                );

        if (result > 0) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Enrollment status updated successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enrollment not found."
            );
        }

        return "redirect:/admin/enrollments";
    }

    // =====================================================
    // CHANGE PAYMENT STATUS
    // POST /admin/enrollments/payment-status
    // =====================================================

    @PostMapping("/payment-status")
    public String changePaymentStatus(
            @RequestParam int enrollmentId,
            @RequestParam String paymentStatus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (enrollmentId <= 0
                || !isValidPaymentStatus(paymentStatus)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid payment status."
            );

            return "redirect:/admin/enrollments";
        }

        int result =
                enrollmentRepo.updatePaymentStatus(
                        enrollmentId,
                        paymentStatus
                );

        if (result > 0) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Payment status updated successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enrollment not found."
            );
        }

        return "redirect:/admin/enrollments";
    }

    private boolean isValidEnrollmentStatus(String status) {

        return status != null
                && List.of(
                        "PENDING",
                        "ACTIVE",
                        "COMPLETED",
                        "DROPPED",
                        "CANCELLED"
                ).contains(status);
    }

    private boolean isValidPaymentStatus(String status) {

        return status != null
                && List.of(
                        "UNPAID",
                        "PARTIAL",
                        "PAID",
                        "REFUNDED"
                ).contains(status);
    }

    private boolean isValidPaymentType(String type) {

        return type != null
                && List.of(
                        "FULL",
                        "INSTALLMENT",
                        "FREE"
                ).contains(type);
    }
}