package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Welcome Banner Summaries
    public Map<String, Object> getBannerSummary() {
        Map<String, Object> map = new HashMap<>();
        int pendingPayments = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM payments WHERE status = 'PENDING'", Integer.class);
        int overdueInstallments = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM installment_schedules WHERE status = 'OVERDUE' OR (status = 'PENDING' AND due_date < CURRENT_DATE)", Integer.class);
        
        map.put("pendingPayments", pendingPayments);
        map.put("overdueInstallments", overdueInstallments);
        return map;
    }

    // 2. Stats Cards Summary
    public Map<String, Object> getStatCardsSummary() {
        Map<String, Object> map = new HashMap<>();
        
        Double monthlyRevenue = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status IN ('SUCCESS', 'VERIFIED') AND MONTH(payment_date) = MONTH(CURRENT_DATE) AND YEAR(payment_date) = YEAR(CURRENT_DATE)", Double.class);
        
        Integer activeStudents = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT user_id) FROM enrollments WHERE status = 'ACTIVE'", Integer.class);
        
        Double outstandingFees = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(amount_due), 0) FROM installment_schedules WHERE status != 'PAID' AND due_date < CURRENT_DATE", Double.class);
        
        Integer activeBatches = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM batches WHERE status = 'ACTIVE' OR status = 'ONGOING' OR status = 'UPCOMING'", Integer.class);

        map.put("monthlyRevenue", monthlyRevenue);
        map.put("activeStudents", activeStudents);
        map.put("outstandingFees", outstandingFees);
        map.put("activeBatches", activeBatches);
        return map;
    }

    // 3. Payments Waiting for Verification Table
    public List<Map<String, Object>> getPendingPayments() {
        String sql = "SELECT p.payment_id, u.full_name AS student_name, u.phone AS student_phone, " +
                     "u.email AS student_email, c.title AS course_name, b.batch_code AS batch_name, " +
                     "p.amount, p.transaction_no, p.payment_date, p.proof_image, p.status " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "LEFT JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE p.status = 'PENDING' ORDER BY p.payment_date DESC LIMIT 5";
        return jdbcTemplate.queryForList(sql);
    }
    // 4. Seats in Open & Upcoming Batches
    public List<Map<String, Object>> getBatchSeatProgress() {
        String sql = "SELECT b.batch_id, b.batch_code, b.title AS course_name, b.start_date, b.max_seats, " +
                     "COUNT(e.enrollment_id) AS enrolled_count " +
                     "FROM batches b " +
                     "LEFT JOIN enrollments e ON b.batch_id = e.batch_id AND e.status = 'ACTIVE' " +
                     "GROUP BY b.batch_id, b.batch_code, b.title, b.start_date, b.max_seats LIMIT 4";
        return jdbcTemplate.queryForList(sql);
    }

    // 5. Overdue Installments List
    public List<Map<String, Object>> getOverdueInstallmentsList() {
        String sql = "SELECT i.schedule_id, u.full_name AS student_name, u.phone, b.batch_code AS batch_name, " +
                     "i.installment_number, i.due_date, i.amount_due, " +
                     "DATEDIFF(CURRENT_DATE, i.due_date) AS days_late " +
                     "FROM installment_schedules i " +
                     "JOIN enrollments e ON i.enrollment_id = e.enrollment_id " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "WHERE i.status != 'PAID' AND i.due_date < CURRENT_DATE " +
                     "ORDER BY i.due_date ASC LIMIT 4";
        return jdbcTemplate.queryForList(sql);
    }
}