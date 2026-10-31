package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ScholarshipMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;

@Repository
public class ScholarshipRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ScholarshipApplicationBean> getApplicationsByUserId(int userId) {
        String sql = """
            SELECT 
                sa.application_id,
                s.name AS title,
                sa.created_at AS applied_date,
                s.coverage_value AS discount_percentage,
                sa.status
            FROM scholarship_applications sa
            JOIN scholarships s ON sa.scholarship_id = s.scholarship_id
            WHERE sa.user_id = ?
            ORDER BY sa.created_at DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ScholarshipApplicationBean bean = new ScholarshipApplicationBean();
            bean.setApplicationId(rs.getInt("application_id"));
            bean.setProgramName(rs.getString("title")); 
            
            if (rs.getDate("applied_date") != null) {
                bean.setAppliedDate(rs.getDate("applied_date").toLocalDate());
            }
            
            bean.setDiscountPercent(rs.getDouble("discount_percentage"));
            bean.setStatus(rs.getString("status"));
            
            return bean;
        }, userId);
    }
 
    public Map<String, Object> getApplicationDetailById(int applicationId) {
        String sql = """
            SELECT 
                sa.application_id,
                s.name AS program_name,
                s.sponsor,
                s.type_code,
                s.coverage_value,
                sa.reason,
                sa.supporting_document,
                sa.status,
                sa.approved_amount,
                sa.reviewed_at,
                sa.created_at
            FROM scholarship_applications sa
            JOIN scholarships s ON sa.scholarship_id = s.scholarship_id
            WHERE sa.application_id = ?
            """;
        try {
            return jdbcTemplate.queryForMap(sql, applicationId);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Get all scholarships
    public List<ScholarshipBean> getAllScholarships() {
        String sql =
                "SELECT scholarship_id, course_id, name, description, "
                + "sponsor, type_code, coverage_value, max_recipients, "
                + "application_start, application_deadline, is_active, "
                + "created_by, created_at, updated_at "
                + "FROM scholarships ORDER BY scholarship_id DESC";

        return jdbcTemplate.query(sql, new ScholarshipMapper());
    }

    // Get scholarship by ID
    public ScholarshipBean getScholarshipById(int scholarshipId) {
        String sql =
                "SELECT scholarship_id, course_id, name, description, "
                + "sponsor, type_code, coverage_value, max_recipients, "
                + "application_start, application_deadline, is_active, "
                + "created_by, created_at, updated_at "
                + "FROM scholarships WHERE scholarship_id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql, new ScholarshipMapper(), scholarshipId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Create scholarship
    public int createScholarship(ScholarshipBean scholarship) {
        String sql =
                "INSERT INTO scholarships "
                + "(course_id, name, description, sponsor, type_code, "
                + "coverage_value, max_recipients, application_start, "
                + "application_deadline, is_active, created_by, "
                + "created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);

            if (scholarship.getCourseId() != null
                    && scholarship.getCourseId() > 0) {
                ps.setInt(1, scholarship.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, scholarship.getName());
            ps.setString(3, scholarship.getDescription());
            ps.setString(4, scholarship.getSponsor());

            String typeCode = scholarship.getTypeCode();

            if (typeCode == null || typeCode.trim().isEmpty()) {
                typeCode = "DEFAULT";
            }

            ps.setString(5, typeCode);

            BigDecimal coverage = scholarship.getCoverageValue();

            ps.setBigDecimal(
                    6, coverage != null ? coverage : BigDecimal.ZERO);

            if (scholarship.getMaxRecipients() != null) {
                ps.setInt(7, scholarship.getMaxRecipients());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            LocalDate startDate = scholarship.getApplicationStart() != null
                    ? scholarship.getApplicationStart()
                    : LocalDate.now();

            ps.setDate(8, java.sql.Date.valueOf(startDate));

            LocalDate deadlineDate =
                    scholarship.getApplicationDeadline() != null
                    ? scholarship.getApplicationDeadline()
                    : LocalDate.now().plusMonths(1);

            ps.setDate(9, java.sql.Date.valueOf(deadlineDate));

            ps.setBoolean(
                    10,
                    scholarship.getIsActive() != null
                            ? scholarship.getIsActive()
                            : true);

            if (scholarship.getCreatedBy() != null) {
                ps.setInt(11, scholarship.getCreatedBy());
            } else {
                ps.setNull(11, Types.INTEGER);
            }

            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null
                ? keyHolder.getKey().intValue()
                : 0;
    }

    // Update scholarship
    public int updateScholarship(
            int scholarshipId, ScholarshipBean scholarship) {

        String sql =
                "UPDATE scholarships SET course_id = ?, name = ?, "
                + "description = ?, sponsor = ?, type_code = ?, "
                + "coverage_value = ?, max_recipients = ?, "
                + "application_start = ?, application_deadline = ?, "
                + "is_active = ?, updated_at = NOW() "
                + "WHERE scholarship_id = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            if (scholarship.getCourseId() != null
                    && scholarship.getCourseId() > 0) {
                ps.setInt(1, scholarship.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, scholarship.getName());
            ps.setString(3, scholarship.getDescription());
            ps.setString(4, scholarship.getSponsor());

            String typeCode = scholarship.getTypeCode();

            if (typeCode == null || typeCode.trim().isEmpty()) {
                typeCode = "DEFAULT";
            }

            ps.setString(5, typeCode);

            BigDecimal coverage = scholarship.getCoverageValue();

            ps.setBigDecimal(
                    6, coverage != null ? coverage : BigDecimal.ZERO);

            if (scholarship.getMaxRecipients() != null) {
                ps.setInt(7, scholarship.getMaxRecipients());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            LocalDate startDate = scholarship.getApplicationStart() != null
                    ? scholarship.getApplicationStart()
                    : LocalDate.now();

            ps.setDate(8, java.sql.Date.valueOf(startDate));

            LocalDate deadlineDate =
                    scholarship.getApplicationDeadline() != null
                    ? scholarship.getApplicationDeadline()
                    : LocalDate.now().plusMonths(1);

            ps.setDate(9, java.sql.Date.valueOf(deadlineDate));

            ps.setBoolean(
                    10,
                    scholarship.getIsActive() != null
                            ? scholarship.getIsActive()
                            : true);

            ps.setInt(11, scholarshipId);

            return ps;
        });
    }

    // Delete scholarship
    public int deleteScholarship(int scholarshipId) {
        String sql =
                "DELETE FROM scholarships WHERE scholarship_id = ?";

        return jdbcTemplate.update(sql, scholarshipId);
    }
    
}