package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate; // 🟢 Added for the inline dynamic enrollment check
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Learning_Managnment_System.JWD_70_lms.Repository.EnrollmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.PaymentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.PaymentBean;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate; // 🟢 Injected to handle dynamic enrollment listings natively

    @GetMapping("/student/checkout")
    public String showCheckoutPage(
            @RequestParam(value = "courseId", required = false, defaultValue = "1") int courseId,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/login";
        }

        // Fetch student base information row matching the session email parameters
        Map<String, Object> student = studentRepository.findByEmail(email);
        
        int userId = 0;
        List<PaymentBean> paymentHistory = new ArrayList<>();
        List<Map<String, Object>> studentEnrollments = new ArrayList<>(); // 🟢 Instantiated enrollment holder

        if (student != null && student.get("user_id") != null) {
            try {
                userId = Integer.parseInt(student.get("user_id").toString());
                
                // 🟢 CRUCIAL FIX: Bind the real database ID to 'studentId' into HTTP Session RAM.
                session.setAttribute("studentId", userId);
                session.setAttribute("userId", userId); // Sync map variant for backup safety
                
                // 🟢 POPULATE HISTORY LOGS: Pulls dynamic payment maps natively to populate the view history popup
                paymentHistory = paymentRepository.getPaymentsByUserId(userId);

                // 🟢 NEW: Pull all active courses or batches the student has registered for
                String enrollmentListSql = """
                    SELECT b.batch_id, b.title as course_title, b.batch_code 
                    FROM enrollments e 
                    JOIN batches b ON e.batch_id = b.batch_id 
                    WHERE e.user_id = ? AND e.status = 'ACTIVE'
                    """;
                studentEnrollments = jdbcTemplate.queryForList(enrollmentListSql, userId);

                // Fallback: If page requested without parameters, use the first assigned batch automatically
                if (session.getAttribute("selectedCourseId") == null && !studentEnrollments.isEmpty() && courseId == 1) {
                    courseId = Integer.parseInt(studentEnrollments.get(0).get("batch_id").toString());
                }
            } catch (Exception e) {
                System.err.println("CHECKOUT CONTROLLER NOTICE -> Handled silent binding parsing mismatch check: " + e.getMessage());
            }
        }

        Map<String, Object> course = enrollmentRepository.getCourseById(courseId);

        if (course == null) {
            course = enrollmentRepository.getCourseById(1);
        }

        List<Map<String, Object>> discounts = enrollmentRepository.getDiscountsByCourseId(courseId);

        List<Map<String, Object>> installments = enrollmentRepository.getInstallmentRules(courseId);

        List<Map<String, Object>> paymentMethods = paymentRepository.getActivePaymentMethods();

        // Populate Thymeleaf context variables natively
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("discounts", discounts);
        model.addAttribute("installments", installments);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("paymentHistory", paymentHistory); // Dynamic history overlay target array hook
        model.addAttribute("studentEnrollments", studentEnrollments); // 🟢 Injected dynamic dropdown options map
        
        model.addAttribute(
                "fullName",
                student != null ? student.get("full_name") : "Student"
        );

        return "checkout";
    }
}
