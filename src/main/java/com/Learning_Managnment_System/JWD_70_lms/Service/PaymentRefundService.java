package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.model.InstallmentBean;

@Service
public class PaymentRefundService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Summary Statistics
    public int countTotalPayments() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM payments", Integer.class);
        return count != null ? count : 0;
    }

    public int countPendingPayments() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM payments WHERE status = 'PENDING'", Integer.class);
        return count != null ? count : 0;
    }

    public int countVerifiedPayments() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM payments WHERE status = 'SUCCESS'", Integer.class);
        return count != null ? count : 0;
    }

    public int countPendingRefunds() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refunds WHERE status = 'PENDING'", Integer.class);
        return count != null ? count : 0;
    }

    // Payment List with Student & Course Details (Null keyword safe ပြုလုပ်ထားသည်)
    public List<Map<String, Object>> getPayments(String keyword, String status) {
        String sql = "SELECT p.*, COALESCE(u.full_name, 'Unknown') AS student_name, " +
                     "COALESCE(u.email, '-') AS student_email, " +
                     "COALESCE(c.title, 'N/A') AS course_name, " +
                     "COALESCE(b.batch_code, 'N/A') AS batch_name " +
                     "FROM payments p " +
                     "LEFT JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN batches b ON e.batch_id = b.batch_id " +
                     "LEFT JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE (COALESCE(u.full_name, '') LIKE ? OR COALESCE(u.email, '') LIKE ? OR COALESCE(p.transaction_no, '') LIKE ?) ";

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql += " AND p.status = '" + status + "'";
        }
        sql += " ORDER BY p.payment_id DESC";

        // Keyword null ဖြစ်နေပါက အလုပ်လုပ်စေရန် ကာကွယ်ခြင်း
        String searchKey = (keyword == null) ? "" : keyword;
        String search = "%" + searchKey + "%";
        
        return jdbcTemplate.queryForList(sql, search, search, search);
    }

    // Refund List (Null keyword safe ပြုလုပ်ထားသည်)
    public List<Map<String, Object>> getRefunds(String keyword, String status) {
        String sql = "SELECT r.*, COALESCE(u.full_name, 'Unknown') AS student_name " +
                     "FROM refunds r " +
                     "LEFT JOIN payments p ON r.payment_id = p.payment_id " +
                     "LEFT JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "WHERE (COALESCE(u.full_name, '') LIKE ? OR COALESCE(r.reason, '') LIKE ?) ";

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql += " AND r.status = '" + status + "'";
        }
        sql += " ORDER BY r.refund_id DESC";

        String searchKey = (keyword == null) ? "" : keyword;
        String search = "%" + searchKey + "%";
        
        return jdbcTemplate.queryForList(sql, search, search);
    }

    // Verify or Reject Payment
    @Transactional
    public boolean processPaymentVerification(int paymentId, String action, int adminId) {
        String newStatus = "verify".equalsIgnoreCase(action) ? "SUCCESS" : "REJECTED";
        String remark = "verify".equalsIgnoreCase(action) ? "Payment verified by admin" : "Payment rejected by admin";

        int count = jdbcTemplate.update(
            "UPDATE payments SET status = ?, processed_by = ?, remark = ?, updated_at = NOW() WHERE payment_id = ?",
            newStatus, adminId, remark, paymentId
        );

        // If verified, update installment schedule status if applicable
        if (count > 0 && "SUCCESS".equals(newStatus)) {
            jdbcTemplate.update(
                "UPDATE installment_schedules SET status = 'PAID' WHERE schedule_id = " +
                "(SELECT schedule_id FROM payments WHERE payment_id = ?)", paymentId
            );
        }
        return count > 0;
    }

    // Update Refund Status
    @Transactional
    public boolean updateRefundStatus(int refundId, String status, int adminId) {
        int count = jdbcTemplate.update(
            "UPDATE refunds SET status = ?, processed_by = ?, processed_at = NOW(), updated_at = NOW() WHERE refund_id = ?",
            status, adminId, refundId
        );
        return count > 0;
    }
    
    
    
    public List<InstallmentBean> getOverdueInstallments(String keyword, String statusFilter) {
        String sql = "SELECT s.*, " +
                     "DATEDIFF(CURDATE(), s.due_date) AS days_overdue, " +
                     "CASE WHEN DATEDIFF(CURDATE(), s.due_date) > 15 THEN 'CRITICAL' ELSE 'OVERDUE' END AS overdue_status, " +
                     "COALESCE(u.full_name, 'N/A') AS student_name, " +
                     "COALESCE(u.email, 'N/A') AS student_email, " +
                     "COALESCE(c.title, 'N/A') AS course_name, " +
                     "COALESCE(b.batch_code, 'N/A') AS batch_name " +
                     "FROM installment_schedules s " +
                     "LEFT JOIN enrollments e ON s.enrollment_id = e.enrollment_id " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN batches b ON e.batch_id = b.batch_id " +
                     "LEFT JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE s.status = 'PENDING' AND s.due_date < CURDATE() " +
                     "AND (u.full_name LIKE ? OR u.email LIKE ? OR c.title LIKE ?) ";

        if ("CRITICAL".equalsIgnoreCase(statusFilter)) {
            sql += " AND DATEDIFF(CURDATE(), s.due_date) > 15";
        } else if ("OVERDUE".equalsIgnoreCase(statusFilter)) {
            sql += " AND DATEDIFF(CURDATE(), s.due_date) <= 15";
        }

        sql += " ORDER BY s.due_date ASC";
        String search = "%" + (keyword == null ? "" : keyword) + "%";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            InstallmentBean bean = new InstallmentBean();
            bean.setSchedule_id(rs.getInt("schedule_id"));
            bean.setEnrollment_id(rs.getInt("enrollment_id"));
            bean.setInstallment_number(rs.getInt("installment_number"));
            bean.setDue_date(rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null);
            bean.setAmount_due(rs.getBigDecimal("amount_due"));
            bean.setPaid_amount(rs.getBigDecimal("paid_amount"));
            bean.setStatus(rs.getString("status"));
            bean.setReminder_sent_at(rs.getTimestamp("reminder_sent_at") != null ? rs.getTimestamp("reminder_sent_at").toLocalDateTime() : null);
            bean.setDays_overdue(rs.getInt("days_overdue"));
            bean.setOverdue_status(rs.getString("overdue_status"));
            bean.setStudent_name(rs.getString("student_name"));
            bean.setStudent_email(rs.getString("student_email"));
            bean.setCourse_name(rs.getString("course_name"));
            bean.setBatch_name(rs.getString("batch_name"));
            return bean;
        }, search, search, search);
    }

    public int countOverdueInstallments() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM installment_schedules WHERE status != 'PAID' AND due_date < CURDATE()", Integer.class);
        return count != null ? count : 0;
    }

    public int countCriticalInstallments() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM installment_schedules WHERE status != 'PAID' AND DATEDIFF(CURDATE(), due_date) > 15", Integer.class);
        return count != null ? count : 0;
    }

    public boolean updateReminderSentAt(int scheduleId) {
        int updated = jdbcTemplate.update(
            "UPDATE installment_schedules SET reminder_sent_at = NOW() WHERE schedule_id = ?", scheduleId);
        return updated > 0;
    }
}