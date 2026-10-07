package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.PaymentBean;

@Repository
public class PaymentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getActivePaymentMethods() {
        String sql = "SELECT * FROM payment_methods WHERE is_active = 1";
        return jdbcTemplate.queryForList(sql);
    }

   
    public Integer getEnrollmentIdByUserId(int userId) {
        String sql = "SELECT enrollment_id FROM enrollments WHERE user_id = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, userId);
        } catch (Exception e) {
            
            String defaultSql = "SELECT enrollment_id FROM enrollments LIMIT 1";
            try {
                return jdbcTemplate.queryForObject(defaultSql, Integer.class);
            } catch (Exception ex) {
                return 1; 
            }
        }
    }

    public int savePayment(PaymentBean payment) {
        String sql = "INSERT INTO payments (enrollment_id, schedule_id, payment_method_id, amount, transaction_no, proof_image, status, remark) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        String combinedRemark = "[" + payment.getPaymentType() + "] " + (payment.getNote() != null ? payment.getNote() : "");

        return jdbcTemplate.update(sql, 
                payment.getEnrollmentId(),
                payment.getScheduleId(),
                payment.getPaymentMethodId(),
                payment.getAmount(),
                payment.getTransactionNo(),
                payment.getProofImage(),
                payment.getStatus(),
                combinedRemark
        );
    }
}