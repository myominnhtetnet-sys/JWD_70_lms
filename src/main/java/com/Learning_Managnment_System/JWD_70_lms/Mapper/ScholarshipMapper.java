package com.Learning_Managnment_System.JWD_70_lms.Mapper;


import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScholarshipMapper implements RowMapper<ScholarshipBean> {

    @Override
    public ScholarshipBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScholarshipBean scholarship = new ScholarshipBean();

        scholarship.setScholarshipId(rs.getInt("scholarship_id"));
        scholarship.setCourseId(rs.getInt("course_id"));
        scholarship.setName(rs.getString("name"));
        scholarship.setDescription(rs.getString("description"));
        scholarship.setSponsor(rs.getString("sponsor"));
        scholarship.setTypeCode(rs.getString("type_code"));
        scholarship.setCoverageValue(rs.getBigDecimal("coverage_value"));
        scholarship.setMaxRecipients(rs.getInt("max_recipients"));
        
        // Handle potential null dates safely
        if (rs.getDate("application_start") != null) {
            scholarship.setApplicationStart(rs.getDate("application_start").toLocalDate());
        }
        if (rs.getDate("application_deadline") != null) {
            scholarship.setApplicationDeadline(rs.getDate("application_deadline").toLocalDate());
        }

        scholarship.setIsActive(rs.getBoolean("is_active"));
        scholarship.setCreatedBy(rs.getInt("created_by"));

        if (rs.getTimestamp("created_at") != null) {
            scholarship.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            scholarship.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return scholarship;
    }
}