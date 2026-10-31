package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.ArrayList;
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
        String sql = """
            SELECT p.*, pm.name AS payment_method_name 
            FROM payments p 
            JOIN enrollments e ON p.enrollment_id = e.enrollment_id 
            LEFT JOIN payment_methods pm ON p.payment_method_id = pm.payment_method_id
            WHERE e.user_id = ? 
            ORDER BY p.payment_id DESC
            """;
        try {            
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                PaymentBean payment = new PaymentBean();
                
                payment.setPaymentId(rs.getInt("payment_id")); 
                payment.setEnrollmentId(rs.getInt("enrollment_id"));
                payment.setScheduleId(rs.getInt("schedule_id"));
                payment.setPaymentMethodId(rs.getInt("payment_method_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setTransactionNo(rs.getString("transaction_no"));
                payment.setProofImage(rs.getString("proof_image"));
                payment.setStatus(rs.getString("status")); // 🟢 Reads 'PENDING','SUCCESS','FAILED','REFUNDED'
                payment.setProcessedBy(rs.getObject("processed_by", Integer.class));
                payment.setPaymentMethodName(rs.getString("payment_method_name"));
                
                String rawRemark = rs.getString("remark");
                payment.setRemark(rawRemark);
                
                if (rawRemark != null && rawRemark.startsWith("[")) {
                    int closingBrace = rawRemark.indexOf("]");
                    if (closingBrace > -1) {
                        payment.setPaymentType(rawRemark.substring(1, closingBrace));
                        payment.setNote(rawRemark.substring(closingBrace + 1).trim());
                    } else {
                        payment.setPaymentType("Full Course Fee");
                        payment.setNote(rawRemark);
                    }
                } else {
                    payment.setPaymentType("Full Course Fee");
                    payment.setNote(rawRemark != null ? rawRemark : "");
                }
                
                java.sql.Timestamp ts = rs.getTimestamp("payment_date");
                if (ts != null) {
                    payment.setPaymentDate(ts.toLocalDateTime());
                }
                
                return payment;
            }, userId);
        } catch (Exception e) {
            System.err.println("REPOSITORY ERROR -> getPaymentsByUserId failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }



}