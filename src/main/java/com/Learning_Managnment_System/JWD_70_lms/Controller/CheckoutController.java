
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Learning_Managnment_System.JWD_70_lms.Repository.EnrollmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.PaymentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/student/checkout")
    public String showCheckoutPage(
            @RequestParam(value = "courseId", required = false, defaultValue = "1") int courseId,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);

        Map<String, Object> course =
                enrollmentRepository.getCourseById(courseId);

        if (course == null) {
            course = enrollmentRepository.getCourseById(1);
        }

        List<Map<String, Object>> discounts =
                enrollmentRepository.getDiscountsByCourseId(courseId);

        List<Map<String, Object>> installments =
                enrollmentRepository.getInstallmentRules(courseId);

        List<Map<String, Object>> paymentMethods =
                paymentRepository.getActivePaymentMethods();

        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("discounts", discounts);
        model.addAttribute("installments", installments);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute(
                "fullName",
                student != null ? student.get("full_name") : "Student"
        );

        return "checkout";
    }
}
