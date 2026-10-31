package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CertificateRepository; // 👈 1. Import ထည့်ရန်
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;

@Controller
public class PublicCertificateController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CertificateRepository certificateRepository; // 👈 2. ဒီနေရာမှာ @Autowired ထည့်ပေးရန် (အနီကြောင်းပျောက်သွားပါမည်)

    @GetMapping("/student/certificate")
    public String showStudentCertificates(@RequestParam(value = "userId", required = false, defaultValue = "0") int userId, Model model) {
        
        // Safety Fallback: Initialize an empty list context right away
        List<Map<String, Object>> certificates = new java.util.ArrayList<>();
        String fullName = "Public Student Profile";
        String email = "Public Access Profile";

        if (userId > 0) {
            // 1. Fetch certificate matrices directly for the targeted public user ID
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

            try {
                List<Map<String, Object>> queriedCertificates = jdbcTemplate.queryForList(sql, userId);
                if (queriedCertificates != null) {
                    certificates = queriedCertificates;
                }

                // 2. Fetch the student profile text fields to display on the public canvas
                String userSql = "SELECT full_name, email FROM users WHERE user_id = ? LIMIT 1";
                List<Map<String, Object>> userResult = jdbcTemplate.queryForList(userSql, userId);
                
                if (userResult != null && !userResult.isEmpty()) {
                    Map<String, Object> userRow = userResult.get(0);
                    if (userRow.get("full_name") != null) {
                        fullName = (String) userRow.get("full_name");
                    }
                    if (userRow.get("email") != null) {
                        email = (String) userRow.get("email");
                    }
                }
            } catch (Exception e) {
                // Graceful trace fallback in case of schema column mismatches during public lookups
                System.err.println("Public certificate data mismatch: " + e.getMessage());
            }
        }

        // 3. Feed your model attributes down to the public Thymeleaf view layer
        model.addAttribute("certificates", certificates);
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);

        return "certificate"; 
    }

//    @GetMapping("/student/certificate/view")
//    public String viewCertificateDetail(@RequestParam("code") String code, HttpSession session, Model model) {
//        String email = (String) session.getAttribute("email");
//        if (email == null) {
//            return "redirect:/login";
//        }
//
//        Map<String, Object> cert = certificateRepository.getCertificateDetailByCode(code);
//        model.addAttribute("cert", cert);
//        model.addAttribute("fullName", session.getAttribute("fullName"));
//
//        return "certificate-detail"; 
//    }
    @GetMapping("/verify-certificate")
    public String showVerificationForm(
            @RequestParam(value = "code", required = false) String certificateCode, 
            Model model) {
        
        if (certificateCode != null && !certificateCode.trim().isEmpty()) {
            model.addAttribute("searchedCode", certificateCode.trim());
            
            Optional<Map<String, Object>> certOpt = Optional.empty();
            
            if (certOpt.isPresent()) {
                model.addAttribute("cert", certOpt.get());
                model.addAttribute("isValidated", true);
            } else {
                model.addAttribute("error", "🚨 No certificate record found matching this validation key token.");
                model.addAttribute("isValidated", false);
            }
        }
        
        return "certificate-verify";
    }
}