package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ScholarshipMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ScholarshipRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ScholarshipBean> getAllScholarships() {
        String sql = "SELECT scholarship_id, course_id, name, description, sponsor, type_code, " +
                     "coverage_value, max_recipients, application_start, application_deadline, " +
                     "is_active, created_by, created_at, updated_at " +
                     "FROM scholarships ORDER BY scholarship_id DESC";
        return jdbcTemplate.query(sql, new ScholarshipMapper());
    }

    public ScholarshipBean getScholarshipById(int scholarshipId) {
        String sql = "SELECT scholarship_id, course_id, name, description, sponsor, type_code, " +
                     "coverage_value, max_recipients, application_start, application_deadline, " +
                     "is_active, created_by, created_at, updated_at " +
                     "FROM scholarships WHERE scholarship_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ScholarshipMapper(), scholarshipId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int createScholarship(ScholarshipBean scholarship) {
        String sql = "INSERT INTO scholarships (course_id, name, description, sponsor, type_code, " +
                     "coverage_value, max_recipients, application_start, application_deadline, " +
                     "is_active, created_by, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            if (scholarship.getCourseId() != null && scholarship.getCourseId() > 0) {
                ps.setInt(1, scholarship.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            
            ps.setString(2, scholarship.getName());
            ps.setString(3, scholarship.getDescription());
            ps.setString(4, scholarship.getSponsor());
            
            // Fallback to a valid existing discount_type code if null/blank
            String typeCode = (scholarship.getTypeCode() != null && !scholarship.getTypeCode().trim().isEmpty()) 
                              ? scholarship.getTypeCode() : "DEFAULT";
            ps.setString(5, typeCode);
            
            ps.setBigDecimal(6, scholarship.getCoverageValue() != null ? scholarship.getCoverageValue() : BigDecimal.ZERO);
            
            if (scholarship.getMaxRecipients() != null) {
                ps.setInt(7, scholarship.getMaxRecipients());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            
            LocalDate startDate = scholarship.getApplicationStart() != null ? scholarship.getApplicationStart() : LocalDate.now();
            ps.setDate(8, java.sql.Date.valueOf(startDate));
            
            LocalDate deadlineDate = scholarship.getApplicationDeadline() != null ? scholarship.getApplicationDeadline() : LocalDate.now().plusMonths(1);
            ps.setDate(9, java.sql.Date.valueOf(deadlineDate));
            
            ps.setBoolean(10, scholarship.getIsActive() != null ? scholarship.getIsActive() : true);
            
            if (scholarship.getCreatedBy() != null) {
                ps.setInt(11, scholarship.getCreatedBy());
            } else {
                ps.setNull(11, Types.INTEGER);
            }
            
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public int updateScholarship(int scholarshipId, ScholarshipBean scholarship) {
        String sql = "UPDATE scholarships SET course_id = ?, name = ?, description = ?, sponsor = ?, " +
                     "type_code = ?, coverage_value = ?, max_recipients = ?, application_start = ?, " +
                     "application_deadline = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE scholarship_id = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            // 1. course_id
            if (scholarship.getCourseId() != null && scholarship.getCourseId() > 0) {
                ps.setInt(1, scholarship.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // 2. name
            ps.setString(2, scholarship.getName());

            // 3. description
            ps.setString(3, scholarship.getDescription());

            // 4. sponsor
            ps.setString(4, scholarship.getSponsor());

            // 5. type_code (Must be a valid existing code in discount_types table)
            String typeCode = (scholarship.getTypeCode() != null && !scholarship.getTypeCode().trim().isEmpty()) 
                              ? scholarship.getTypeCode() : "DEFAULT";
            ps.setString(5, typeCode);

            // 6. coverage_value
            ps.setBigDecimal(6, scholarship.getCoverageValue() != null ? scholarship.getCoverageValue() : BigDecimal.ZERO);

            // 7. max_recipients
            if (scholarship.getMaxRecipients() != null) {
                ps.setInt(7, scholarship.getMaxRecipients());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            // 8. application_start
            LocalDate startDate = scholarship.getApplicationStart() != null ? scholarship.getApplicationStart() : LocalDate.now();
            ps.setDate(8, java.sql.Date.valueOf(startDate));

            // 9. application_deadline
            LocalDate deadlineDate = scholarship.getApplicationDeadline() != null ? scholarship.getApplicationDeadline() : LocalDate.now().plusMonths(1);
            ps.setDate(9, java.sql.Date.valueOf(deadlineDate));

            // 10. is_active
            ps.setBoolean(10, scholarship.getIsActive() != null ? scholarship.getIsActive() : true);

            // 11. scholarship_id (WHERE clause parameter)
            ps.setInt(11, scholarshipId);

            return ps;
        });
    }

    public int deleteScholarship(int scholarshipId) {
        String sql = "DELETE FROM scholarships WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, scholarshipId);
    }
}