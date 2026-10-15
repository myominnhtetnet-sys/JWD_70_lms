package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CertificateRepository; // 👈 1. Import ထည့်ရန်
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PublicCertificateController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CertificateRepository certificateRepository; // 👈 2. ဒီနေရာမှာ @Autowired ထည့်ပေးရန် (အနီကြောင်းပျောက်သွားပါမည်)

    @GetMapping("/student/certificate")
    public String showStudentCertificates(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student != null) {
            Object idObj = student.get("user_id");
            int userId = (idObj instanceof Number) ? ((Number) idObj).intValue() : 0;

            String sql = """
                SELECT 
                    c.certificate_code,
                    COALESCE(b.title, 'General Course') AS course_title,
                    c.issued_at,
                    c.status,
                    c.file_path
                FROM certificates c
                JOIN enrollments e ON c.enrollment_id = e.enrollment_id
                LEFT JOIN batches b ON e.batch_id = b.batch_id
                WHERE e.user_id = ?
                """;

            List<Map<String, Object>> certificates = jdbcTemplate.queryForList(sql, userId);
            
            model.addAttribute("certificates", certificates);
            model.addAttribute("fullName", student.get("full_name"));
            model.addAttribute("email", email);
        }

        return "certificate"; 
    }

    @GetMapping("/student/certificate/view")
    public String viewCertificateDetail(@RequestParam("code") String code, HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> cert = certificateRepository.getCertificateDetailByCode(code);
        model.addAttribute("cert", cert);
        model.addAttribute("fullName", session.getAttribute("fullName"));

        return "certificate-detail"; 
    }
}