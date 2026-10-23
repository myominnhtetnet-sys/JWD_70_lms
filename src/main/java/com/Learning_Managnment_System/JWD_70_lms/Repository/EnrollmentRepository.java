package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentDetailBean;
import com.Learning_Managnment_System.JWD_70_lms.model.InstallmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.PaymentBean;

@Repository
public class EnrollmentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =====================================================
    // ROW MAPPER
    // =====================================================

    private final RowMapper<EnrollmentBean> enrollmentMapper =
            new RowMapper<EnrollmentBean>() {

        @Override
        public EnrollmentBean mapRow(
                ResultSet rs,
                int rowNum) throws SQLException {

            EnrollmentBean e = new EnrollmentBean();

            e.setEnrollment_id(rs.getInt("enrollment_id"));
            e.setUser_id(rs.getInt("user_id"));
            e.setBatch_id(rs.getInt("batch_id"));

            e.setStudent_name(rs.getString("student_name"));
            e.setStudent_email(rs.getString("student_email"));

            e.setCourse_name(rs.getString("course_name"));
            e.setBatch_name(rs.getString("batch_name"));

            e.setOriginal_amount(
                    rs.getBigDecimal("original_amount"));

            e.setDiscount_amount(
                    rs.getBigDecimal("discount_amount"));

            e.setScholarship_amount(
                    rs.getBigDecimal("scholarship_amount"));

            e.setFinal_amount(
                    rs.getBigDecimal("final_amount"));

            e.setPayment_type(
                    rs.getString("payment_type"));

            e.setPayment_status(
                    rs.getString("payment_status"));

            e.setStatus(
                    rs.getString("status"));

            e.setStatus_reason(
                    rs.getString("status_reason"));

            if (rs.getTimestamp("enrolled_at") != null) {
                e.setEnrolled_at(
                        rs.getTimestamp("enrolled_at")
                                .toLocalDateTime());
            }

            return e;
        }
    };
    
    public EnrollmentDetailBean getEnrollmentById(int enrollmentId) {
        String sql = """
            SELECT e.enrollment_id, u.full_name AS student_name, u.email AS student_email,
                   c.title AS course_title, b.title AS batch_code, e.original_amount, e.discount_amount, 
                   e.scholarship_amount, e.final_amount, e.payment_type, e.payment_status, 
                   e.status AS enrollment_status, e.enrolled_at
            FROM enrollments e
            JOIN users u ON e.user_id = u.user_id
            JOIN batches b ON e.batch_id = b.batch_id
            JOIN courses c ON b.course_id = c.course_id
            WHERE e.enrollment_id = ?
            """;
        
        List<EnrollmentDetailBean> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            EnrollmentDetailBean dto = new EnrollmentDetailBean();
            dto.setEnrollmentId(rs.getInt("enrollment_id"));
            dto.setStudentName(rs.getString("student_name"));
            dto.setStudentEmail(rs.getString("student_email"));
            dto.setCourseTitle(rs.getString("course_title"));
            dto.setBatchCode(rs.getString("batch_code"));
            dto.setTotalFee(rs.getBigDecimal("original_amount"));
            dto.setDiscountAmount(rs.getBigDecimal("discount_amount"));
            dto.setScholarshipAmount(rs.getBigDecimal("scholarship_amount"));
            dto.setFinalAmount(rs.getBigDecimal("final_amount"));
            dto.setPaymentType(rs.getString("payment_type"));
            dto.setPaymentStatus(rs.getString("payment_status"));
            dto.setEnrollmentStatus(rs.getString("enrollment_status"));
            if(rs.getTimestamp("enrolled_at") != null) {
                dto.setCreatedAt(rs.getTimestamp("enrolled_at").toLocalDateTime().toLocalDate());
            }
            return dto;
        }, enrollmentId);

        return list.isEmpty() ? null : list.get(0);
    }

    // 2. Installment Schedules ယူရန်
    public List<InstallmentBean> getInstallmentsByEnrollmentId(int enrollmentId) {
        String sql = "SELECT * FROM installment_schedules WHERE enrollment_id = ? ORDER BY installment_number ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InstallmentBean.class), enrollmentId);
    }

    // 3. Payment History ယူရန်
    public List<PaymentBean> getPaymentsByEnrollmentId(int enrollmentId) {
        String sql = "SELECT * FROM payments WHERE enrollment_id = ? ORDER BY payment_date DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PaymentBean.class), enrollmentId);
    }

    // =====================================================
    // GET ALL ENROLLMENTS
    // =====================================================

    public List<EnrollmentBean> getEnrollments() {

        String sql = """
            SELECT
                e.enrollment_id,
                e.user_id,
                e.batch_id,

                u.full_name AS student_name,
                u.email AS student_email,

                c.title AS course_name,
                b.title AS batch_name,

                e.enrolled_at,
                e.original_amount,
                e.discount_amount,
                e.scholarship_amount,
                e.final_amount,
                e.payment_type,
                e.payment_status,
                e.status,
                e.status_reason

            FROM enrollments e

            INNER JOIN users u
                ON u.user_id = e.user_id

            INNER JOIN batches b
                ON b.batch_id = e.batch_id

            INNER JOIN courses c
                ON c.course_id = b.course_id

            WHERE u.deleted_at IS NULL

            ORDER BY e.enrollment_id DESC
            """;

        return jdbcTemplate.query(sql, enrollmentMapper);
    }

    // =====================================================
    // GET ENROLLMENTS WITH SEARCH AND FILTER
    // =====================================================

    public List<EnrollmentBean> searchEnrollments(
            String keyword,
            String status,
            String paymentStatus) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                e.enrollment_id,
                e.user_id,
                e.batch_id,

                u.full_name AS student_name,
                u.email AS student_email,

                c.title AS course_name,
                b.title AS batch_name,

                e.enrolled_at,
                e.original_amount,
                e.discount_amount,
                e.scholarship_amount,
                e.final_amount,
                e.payment_type,
                e.payment_status,
                e.status,
                e.status_reason

            FROM enrollments e

            INNER JOIN users u
                ON u.user_id = e.user_id

            INNER JOIN batches b
                ON b.batch_id = e.batch_id

            INNER JOIN courses c
                ON c.course_id = b.course_id

            WHERE u.deleted_at IS NULL
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {

            sql.append("""
                AND (
                    LOWER(u.full_name) LIKE ?
                    OR LOWER(u.email) LIKE ?
                    OR LOWER(c.title) LIKE ?
                    OR LOWER(b.title) LIKE ?
                )
                """);

            String search = "%"
                    + keyword.trim().toLowerCase()
                    + "%";

            params.add(search);
            params.add(search);
            params.add(search);
            params.add(search);
        }

        if (status != null
                && !status.isBlank()
                && !"ALL".equalsIgnoreCase(status)) {

            sql.append(" AND e.status = ? ");
            params.add(status);
        }

        if (paymentStatus != null
                && !paymentStatus.isBlank()
                && !"ALL".equalsIgnoreCase(paymentStatus)) {

            sql.append(" AND e.payment_status = ? ");
            params.add(paymentStatus);
        }

        sql.append(" ORDER BY e.enrollment_id DESC ");

        return jdbcTemplate.query(
                sql.toString(),
                enrollmentMapper,
                params.toArray());
    }

    // =====================================================
    // GET ACTIVE STUDENTS
    // =====================================================

    public List<Map<String, Object>> getStudents() {

        String sql = """
            SELECT
                user_id,
                full_name,
                email

            FROM users

            WHERE role_id = 3
              AND status = 'ACTIVE'
              AND deleted_at IS NULL

            ORDER BY full_name
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // =====================================================
    // GET AVAILABLE BATCHES
    // =====================================================

    public List<Map<String, Object>> getBatches() {

        String sql = """
            SELECT
                b.batch_id,
                b.batch_code,
                b.title AS batch_name,
                c.title AS course_name

            FROM batches b

            INNER JOIN courses c
                ON c.course_id = b.course_id

            WHERE c.deleted_at IS NULL

            ORDER BY c.title, b.title
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // =====================================================
    // CREATE ENROLLMENT
    // =====================================================

    public int createEnrollment(EnrollmentBean e) {

        String sql = """
            INSERT INTO enrollments (
                user_id,
                batch_id,
                original_amount,
                discount_amount,
                scholarship_amount,
                final_amount,
                payment_type,
                payment_status,
                status
            )

            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                e.getUser_id(),
                e.getBatch_id(),
                e.getOriginal_amount(),
                e.getDiscount_amount(),
                e.getScholarship_amount(),
                e.getFinal_amount(),
                e.getPayment_type(),
                e.getPayment_status(),
                e.getStatus()
        );
    }

    // =====================================================
    // UPDATE ENROLLMENT STATUS
    // =====================================================

    public int updateEnrollmentStatus(
            int enrollmentId,
            String status) {

        String sql = """
            UPDATE enrollments
            SET status = ?
            WHERE enrollment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                status,
                enrollmentId);
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // =====================================================

    public int updatePaymentStatus(
            int enrollmentId,
            String paymentStatus) {

        String sql = """
            UPDATE enrollments
            SET payment_status = ?
            WHERE enrollment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                paymentStatus,
                enrollmentId);
    }

    // =====================================================
    // COUNT ALL ENROLLMENTS
    // =====================================================

    public int countEnrollments() {

        String sql = """
            SELECT COUNT(*)
            FROM enrollments
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class);

        return count == null ? 0 : count;
    }

    // =====================================================
    // COUNT ACTIVE ENROLLMENTS
    // =====================================================

    public int countActiveEnrollments() {

        String sql = """
            SELECT COUNT(*)
            FROM enrollments
            WHERE status = 'ACTIVE'
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class);

        return count == null ? 0 : count;
    }

    // =====================================================
    // COUNT PENDING ENROLLMENTS
    // =====================================================

    public int countPendingEnrollments() {

        String sql = """
            SELECT COUNT(*)
            FROM enrollments
            WHERE status = 'PENDING'
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class);

        return count == null ? 0 : count;
    }

    // =====================================================
    // TOTAL ENROLLMENT AMOUNT
    // =====================================================

    public BigDecimal totalEnrollmentAmount() {

        String sql = """
            SELECT COALESCE(SUM(final_amount), 0)
            FROM enrollments
            WHERE status <> 'CANCELLED'
            """;

        BigDecimal total = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class);

        return total == null
                ? BigDecimal.ZERO
                : total;
    }
}