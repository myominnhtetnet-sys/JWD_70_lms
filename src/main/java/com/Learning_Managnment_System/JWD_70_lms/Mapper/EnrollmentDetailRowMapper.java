package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentDetailBean;

public class EnrollmentDetailRowMapper implements RowMapper<EnrollmentDetailBean> {

    @Override
    public EnrollmentDetailBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        EnrollmentDetailBean detail = new EnrollmentDetailBean();
        
        detail.setEnrollmentId(rs.getInt("enrollment_id"));
        detail.setStudentName(rs.getString("student_name"));
        detail.setStudentEmail(rs.getString("student_email"));
        detail.setCourseTitle(rs.getString("course_title"));
        detail.setBatchCode(rs.getString("batch_code"));
        
        // Fee Breakdown (BigDecimal)
        detail.setTotalFee(rs.getBigDecimal("original_amount"));
        detail.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        detail.setScholarshipAmount(rs.getBigDecimal("scholarship_amount"));
        detail.setFinalAmount(rs.getBigDecimal("final_amount"));
        
        detail.setPaymentType(rs.getString("payment_type"));
        detail.setPaymentStatus(rs.getString("payment_status"));
        detail.setEnrollmentStatus(rs.getString("status"));
        
        // LocalDate Mapping safely
        if (rs.getDate("created_at") != null) {
            detail.setCreatedAt(rs.getDate("created_at").toLocalDate());
        }
        
        return detail;
    }
}