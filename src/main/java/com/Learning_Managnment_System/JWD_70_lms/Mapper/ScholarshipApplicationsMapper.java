package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationsBean;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScholarshipApplicationsMapper implements RowMapper<ScholarshipApplicationsBean> {

    @Override
    public ScholarshipApplicationsBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScholarshipApplicationsBean application = new ScholarshipApplicationsBean();

        // Mapping columns from scholarship_applications table
        application.setApplicationId(rs.getInt("application_id"));
        application.setScholarshipId(rs.getInt("scholarship_id"));
        application.setUserId(rs.getInt("user_id"));
        application.setBatchId(rs.getInt("batch_id"));
        application.setReason(rs.getString("reason"));
        application.setSupportingDocument(rs.getString("supporting_document"));
        application.setStatus(rs.getString("status"));
        application.setApprovedAmount(rs.getBigDecimal("approved_amount"));
        application.setReviewedBy(rs.getInt("reviewed_by"));

        // Handle potential null timestamps safely
        if (rs.getTimestamp("reviewed_at") != null) {
            application.setReviewedAt(rs.getTimestamp("reviewed_at").toLocalDateTime());
        }

        application.setReviewNotes(rs.getString("review_notes"));

        if (rs.getTimestamp("created_at") != null) {
            application.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            application.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        try {
            application.setStudentName(rs.getString("student_name"));
        } catch (SQLException ignored) {
            // column not present in query
        }

        try {
            application.setStudentEmail(rs.getString("student_email"));
        } catch (SQLException ignored) {
            // column not present in query
        }

        try {
            application.setScholarshipName(rs.getString("scholarship_name"));
        } catch (SQLException ignored) {
            // column not present in query
        }

        return application;
    }
}