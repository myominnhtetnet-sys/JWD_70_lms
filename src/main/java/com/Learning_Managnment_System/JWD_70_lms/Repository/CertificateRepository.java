package com.Learning_Managnment_System.JWD_70_lms.Repository;

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