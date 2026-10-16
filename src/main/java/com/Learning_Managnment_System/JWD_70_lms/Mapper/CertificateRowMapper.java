package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;

public class CertificateRowMapper implements RowMapper<CertificateBean> {
	
	@Override
    public CertificateBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        CertificateBean cert = new CertificateBean();
        
        cert.setCertificateId(rs.getLong("certificate_id"));
        cert.setEnrollmentId(rs.getLong("enrollment_id"));
        cert.setCertificateCode(rs.getString("certificate_code"));
        cert.setFilePath(rs.getString("file_path"));
        
        Timestamp issuedAt = rs.getTimestamp("issued_at");
        if (issuedAt != null) {
            cert.setIssuedAt(issuedAt.toLocalDateTime());
        }
        
        cert.setIssuedBy(rs.getLong("issued_by"));
        cert.setStatus(rs.getString("status"));
        cert.setRevokedReason(rs.getString("revoked_reason"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            cert.setCreatedAt(createdAt.toLocalDateTime());
        }

        // JOIN Query မှ ပါလာနိုင်သော Field များကို Try-Catch ဖြင့် ဖတ်ရှုခြင်း
        try { cert.setStudentName(rs.getString("student_name")); } catch (SQLException e) {}
        try { cert.setStudentEmail(rs.getString("student_email")); } catch (SQLException e) {}
        try { cert.setBatchCode(rs.getString("batch_code")); } catch (SQLException e) {}
        try { cert.setCourseTitle(rs.getString("course_title")); } catch (SQLException e) {}
        try { cert.setIssuerName(rs.getString("issuer_name")); } catch (SQLException e) {}

        return cert;
    }

}
