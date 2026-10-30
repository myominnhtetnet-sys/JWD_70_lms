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
        // Database ဇယားတွင် payment_type column မရှိပါက remark ထဲသို့ ပုံစံတကျ ထည့်သွင်းပေးခြင်း
        String sql = "INSERT INTO payments (enrollment_id, schedule_id, payment_method_id, amount, transaction_no, proof_image, status, remark) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Payment Type နှင့် Note ကို ပေါင်း၍ remark ထဲသို့ သိမ်းဆည်းခြင်း
        String combinedRemark = "[" + (payment.getPaymentType() != null ? payment.getPaymentType() : "Full Course Fee") + "] " 
                                + (payment.getNote() != null && !payment.getNote().isEmpty() ? payment.getNote() : "");

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
    
    public List<PaymentBean> getPaymentsByUserId(int userId) {
        String sql = "SELECT p.* FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.id " +
                     "WHERE e.user_id = ? ORDER BY p.payment_id DESC"; // Mapped to payment_id column ranking
                     
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PaymentBean payment = new PaymentBean();
            
            // 🟢 Fully synchronized with your custom PaymentBean fields
            payment.setPaymentId(rs.getInt("payment_id")); 
            payment.setEnrollmentId(rs.getInt("enrollment_id"));
            payment.setScheduleId(rs.getInt("schedule_id"));
            payment.setPaymentMethodId(rs.getInt("payment_method_id"));
            payment.setAmount(rs.getDouble("amount"));
            payment.setTransactionNo(rs.getString("transaction_no"));
            payment.setPaymentType(rs.getString("payment_type"));
            payment.setNote(rs.getString("note"));
            payment.setProofImage(rs.getString("proof_image"));
            payment.setStatus(rs.getString("status"));
            payment.setProcessedBy(rs.getObject("processed_by", Integer.class));
            payment.setRemark(rs.getString("remark"));
            
            // Safe LocalDateTime conversion using your paymentDate property field
            java.sql.Timestamp ts = rs.getTimestamp("payment_date");
            if (ts != null) {
                payment.setPaymentDate(ts.toLocalDateTime());
            }
            
            return payment;
        }, userId);
    }


}