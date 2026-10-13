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
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "LEFT JOIN users issuer ON c.issued_by = issuer.user_id " +
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
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "LEFT JOIN users issuer ON c.issued_by = issuer.user_id " +
                     "WHERE c.certificate_id = ?";
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

}
