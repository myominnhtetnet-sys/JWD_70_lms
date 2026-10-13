package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student/awards")
public class AwardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 🟢 Renders the dynamic Grades Transcript Ledger & Certificate Wallet.
     * URL: http://localhost:8080/student/awards
     */
    @GetMapping
    public String showAwardsDashboard(Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        String sql = "SELECT g.grade_id, g.enrollment_id, c.title AS course_title, " +
                     "g.assignment_score, g.exam_score, g.attendance_percent, g.total_score, " +
                     "g.grade_letter, g.result, cert.certificate_id, cert.certificate_code, " +
                     "cert.issued_at, cert.status AS cert_status " +
                     "FROM lms_db.grades g " +
                     "JOIN lms_db.enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN lms_db.courses c ON b.course_id = c.course_id " +
                     "JOIN lms_db.users u ON e.user_id = u.user_id " +
                     "LEFT JOIN lms_db.certificates cert ON cert.enrollment_id = e.enrollment_id " +
                     "WHERE u.email = ? AND g.published_at IS NOT NULL " +
                     "ORDER BY g.published_at DESC";

        // 🟢 FIXED: Maps row values into structured hierarchical object layers cleanly
        List<StudentGradeBean> awardsList = jdbcTemplate.query(sql, (rs, rowNum) -> {
            StudentGradeBean grade = new StudentGradeBean();
            grade.setGradeId(rs.getInt("grade_id"));
            grade.setEnrollmentId(rs.getInt("enrollment_id"));
            grade.setCourseTitle(rs.getString("course_title"));
            grade.setAssignmentScore(((Number) rs.getObject("assignment_score")).doubleValue());
            grade.setExamScore(((Number) rs.getObject("exam_score")).doubleValue());
            grade.setAttendancePercent(((Number) rs.getObject("attendance_percent")).doubleValue());
            grade.setTotalScore(((Number) rs.getObject("total_score")).doubleValue());
            grade.setGradeLetter(rs.getString("grade_letter"));
            grade.setResult(rs.getString("result"));
            
            int certId = rs.getInt("certificate_id");
            if (!rs.wasNull()) {
                CertificateBean certificate = new CertificateBean();
                certificate.setCertificateId(certId);
                certificate.setCertificateCode(rs.getString("certificate_code"));
                certificate.setIssuedAt(rs.getTimestamp("issued_at").toString().substring(0, 10));
                certificate.setCertStatus(rs.getString("cert_status"));
                
                grade.setCertificate(certificate); // Bind object reference
            }
            return grade;
        }, studentEmail);

        model.addAttribute("awards", awardsList);
        return "student_awards_dashboard";
    }

    @GetMapping("/certificate/{id}")
    public String viewPrintableCertificate(@PathVariable("id") int certificateId, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        String sql = "SELECT cert.certificate_code, c.title AS course_title, u.full_name, cert.issued_at " +
                     "FROM lms_db.certificates cert " +
                     "JOIN lms_db.enrollments e ON cert.enrollment_id = e.enrollment_id " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN lms_db.courses c ON b.course_id = c.course_id " +
                     "JOIN lms_db.users u ON e.user_id = u.user_id " +
                     "WHERE cert.certificate_id = ? AND u.email = ? AND cert.status = 'ISSUED' LIMIT 1";

        // 🟢 FIXED: Changed query mapper from CertificateBean to StudentGradeBean composition matrix
        List<StudentGradeBean> matches = jdbcTemplate.query(sql, (rs, rowNum) -> {
            StudentGradeBean grade = new StudentGradeBean();
            grade.setCourseTitle(rs.getString("course_title")); // Parent container sets course title string
            
            CertificateBean cert = new CertificateBean();
            cert.setCertificateCode(rs.getString("certificate_code"));
            cert.setStudentName(rs.getString("full_name"));
            cert.setIssuedAt(rs.getTimestamp("issued_at").toString().substring(0, 10));
            
            grade.setCertificate(cert); // Bind child credential package token pocket
            return grade;
        }, certificateId, studentEmail);

        if (matches.isEmpty()) {
            return "redirect:/student/awards";
        }

        model.addAttribute("diploma", matches.get(0));
        return "view_award_diploma";
    }
}
