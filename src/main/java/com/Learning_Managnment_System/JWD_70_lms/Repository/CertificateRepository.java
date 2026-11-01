package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.CertificateRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
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
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses cr ON b.course_id = cr.course_id " +
                     "LEFT JOIN users issuer ON c.issued_by = issuer.user_id " +
                     "WHERE r.role_name = 'Student' " +
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

    public int revokeCertificate(Long certificateId, String reason) {
        String sql = "UPDATE certificates SET status = 'REVOKED', revoked_reason = ? WHERE certificate_id = ?";
        return jdbcTemplate.update(sql, reason, certificateId);
    }

    public List<BatchBean.BatchInfo> findAllCompletedBatches() {
        String sql = """
            SELECT b.batch_id, b.batch_code, cr.title AS course_title 
            FROM batches b 
            JOIN courses cr ON b.course_id = cr.course_id 
            ORDER BY b.batch_id DESC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BatchBean.BatchInfo batch = new BatchBean.BatchInfo();
            batch.setBatchId(rs.getLong("batch_id"));
            batch.setBatchCode(rs.getString("batch_code"));
            batch.setCourseTitle(rs.getString("course_title"));
            return batch;
        });
    }

    public int countCompletedStudentsByBatchId(Long batchId) {
        String sql = """
            SELECT COUNT(*) 
            FROM enrollments e 
            JOIN users u ON e.user_id = u.user_id 
            JOIN roles r ON u.role_id = r.role_id 
            JOIN batches b ON e.batch_id = b.batch_id 
            WHERE r.role_name = 'Student' AND b.batch_id = ? 
            AND e.status = 'COMPLETED'
        """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, batchId);
        return count != null ? count : 0;
    }

    // 🟢 2. အဲ့ဒီ batch ထဲက COMPLETED ဖြစ်တဲ့ ကျောင်းသားတွေအတွက် Certificate ထုတ်ပြီးသား ဘယ်နှစ်ယောက်ရှိလဲ စစ်ရန်
    public int countIssuedCertificatesByBatchId(Long batchId) {
        String sql = """
            SELECT COUNT(c.certificate_id) 
            FROM certificates c
            JOIN enrollments e ON c.enrollment_id = e.enrollment_id
            JOIN batches b ON e.batch_id = b.batch_id
            WHERE b.batch_id = ? AND c.status = 'ISSUED' AND e.status = 'COMPLETED'
        """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, batchId);
        return count != null ? count : 0;
    }

    public List<Long> findEligibleEnrollmentIdsByBatchId(Long batchId) {
        String sql = """
            SELECT e.enrollment_id 
            FROM enrollments e 
            JOIN users u ON e.user_id = u.user_id 
            JOIN roles r ON u.role_id = r.role_id 
            JOIN batches b ON e.batch_id = b.batch_id 
            WHERE r.role_name = 'Student' AND b.batch_id = ? 
            AND e.status = 'COMPLETED'
            AND e.enrollment_id NOT IN (SELECT enrollment_id FROM certificates WHERE status = 'ISSUED')
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("enrollment_id"), batchId);
    }

    public int issueCertificatesForBatch(Long batchId, Integer issuedBy) {
        List<Long> eligibleEnrollmentIds = findEligibleEnrollmentIdsByBatchId(batchId);
        if (eligibleEnrollmentIds.isEmpty()) {
            return 0;
        }

        String sql = "INSERT INTO certificates (enrollment_id, certificate_code, file_path, issued_at, issued_by, status, created_at) " +
                     "VALUES (?, ?, ?, NOW(), ?, 'ISSUED', NOW())";

        int successCount = 0;
        for (Long enrollmentId : eligibleEnrollmentIds) {
            String certCode = "CERT-" + System.currentTimeMillis() + "-" + enrollmentId;
            String filePath = "certs/cert_" + enrollmentId + ".pdf";

            int rows = jdbcTemplate.update(sql, enrollmentId, certCode, filePath, issuedBy);
            if (rows > 0) {
                successCount++;
            }
        }
        return successCount;
    }

    public List<CertificateBean> findCertificatesByUserId(int userId) {
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
                     "WHERE e.user_id = ? AND r.role_name = 'Student' " +
                     "ORDER BY c.created_at DESC";
        return jdbcTemplate.query(sql, new CertificateRowMapper(), userId);
    }

    public Optional<CertificateBean> verifyCertificateCode(String certificateCode) {
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
                     "WHERE c.certificate_code = ? AND r.role_name = 'Student' " +
                     "LIMIT 1";
        try {
            CertificateBean cert = jdbcTemplate.queryForObject(sql, new CertificateRowMapper(), certificateCode.trim());
            return Optional.of(cert);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}