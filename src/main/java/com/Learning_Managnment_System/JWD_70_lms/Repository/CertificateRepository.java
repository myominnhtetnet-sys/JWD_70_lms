package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.CertificateRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;

@Repository
public class CertificateRepository {
	
	private final JdbcTemplate jdbcTemplate;

    public CertificateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. ကျောင်းသားများ၏ Certificate များကိုသာ ရှာဖွေဖော်ပြခြင်း
    public List<CertificateBean> findAllWithDetails() {
        String sql = "SELECT c.*, " +
                     "u.full_name AS student_name, " +
                     "u.email AS student_email, " +
                     "b.batch_code AS batch_code, " +
                     "cr.title AS course_title, " +
                     "issuer.full_name AS issuer_name " +
                     "FROM certificates c " +
                     "JOIN enrollments e ON c.enrollment_id = e.enrollment_id " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "LEFT JOIN users issuer ON c.issued_by = issuer.user_id " +
                     "WHERE r.role_name = 'Student' " + // 🟢 ကျောင်းသားများသာ
                     "ORDER BY c.created_at DESC";
        return jdbcTemplate.query(sql, new CertificateRowMapper());
    }

    public CertificateBean findById(Long id) {
        String sql = "SELECT c.*, " +
                     "u.full_name AS student_name, " +
                     "u.email AS student_email, " +
                     "b.batch_code AS batch_code, " +
                     "cr.title AS course_title, " +
                     "issuer.full_name AS issuer_name " +
                     "FROM certificates c " +
                     "JOIN enrollments e ON c.enrollment_id = e.enrollment_id " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "LEFT JOIN users issuer ON c.issued_by = issuer.user_id " +
                     "WHERE c.certificate_id = ? AND r.role_name = 'Student'";
        return jdbcTemplate.queryForObject(sql, new CertificateRowMapper(), id);
    }

    public int save(CertificateBean cert) {
        String sql = "INSERT INTO certificates (enrollment_id, certificate_code, file_path, issued_at, issued_by, status, created_at) " +
                     "VALUES (?, ?, ?, NOW(), ?, 'ISSUED', NOW())";
        return jdbcTemplate.update(sql, cert.getEnrollmentId(), cert.getCertificateCode(), cert.getFilePath(), cert.getIssuedBy());
    }

    public int revokeCertificate(Long certificateId, String reason) {
        String sql = "UPDATE certificates SET status = 'REVOKED', revoked_reason = ? WHERE certificate_id = ?";
        return jdbcTemplate.update(sql, reason, certificateId);
    }

    // 2. ကျောင်းသား (Student) များ၏ Enrollment, Course, Batch အချက်အလက်များကို Modal Dropdown အတွက် သီးသန့်ဆွဲထုတ်ခြင်း
    public List<CertificateBean> findAllEnrollmentsWithStudentAndCourse() {
        String sql = "SELECT e.enrollment_id, u.full_name AS student_name, u.email AS student_email, " +
                     "b.batch_code AS batch_code, cr.title AS course_title " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "WHERE r.role_name = 'Student' " + // 🟢 ဆရာမများ မပါစေဘဲ ကျောင်းသားများကိုသာ ရွေးထုတ်ရန်
                     "ORDER BY e.enrollment_id DESC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CertificateBean bean = new CertificateBean();
            bean.setEnrollmentId(rs.getLong("enrollment_id"));
            bean.setStudentName(rs.getString("student_name"));
            bean.setStudentEmail(rs.getString("student_email"));
            bean.setBatchCode(rs.getString("batch_code"));
            bean.setCourseTitle(rs.getString("course_title"));
            return bean;
        });
    }

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CertificateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getCertificateByCode(String certificateCode) {
        String sql = """
            SELECT 
                c.certificate_code,
                u.full_name AS student_name,
                COALESCE(co.title, 'General Course') AS course_title,
                c.issued_at,
                c.status,
                c.revoked_reason
            FROM certificates c
            JOIN enrollments e ON c.enrollment_id = e.enrollment_id
            JOIN users u ON e.user_id = u.user_id
            LEFT JOIN batches b ON e.batch_id = b.batch_id
            LEFT JOIN courses co ON b.course_id = co.course_id
            WHERE c.certificate_code = ?
            """;
        try {
            return jdbcTemplate.queryForMap(sql, certificateCode);
        } catch (Exception e) {
            return null; 
        }
    }
 
    public Optional<Map<String, Object>> verifyCertificateCode(String certificateCode) {
        String sql = """
            SELECT 
                c.certificate_id,
                c.certificate_code,
                c.file_path,
                c.issued_at,
                c.status,
                c.revoked_reason,
                u.full_name AS student_name,
                u.email AS student_email,
                COALESCE(b.title, 'General Course') AS course_title
            FROM certificates c
            JOIN enrollments e ON c.enrollment_id = e.enrollment_id
            JOIN users u ON e.user_id = u.user_id
            LEFT JOIN batches b ON e.batch_id = b.batch_id
            WHERE c.certificate_code = ?
            LIMIT 1
            """;
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, certificateCode.trim());
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}