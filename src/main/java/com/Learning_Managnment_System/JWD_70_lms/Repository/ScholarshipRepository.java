package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<Map<String, Object>> findAvailablePrograms(int userId) {
       
        String sql = """
            SELECT 
                s.scholarship_id, 
                s.name AS program_name, 
                s.sponsor, 
                s.description, 
                s.coverage_value AS max_amount 
            FROM scholarships s
            WHERE s.is_active = 1
              AND NOT EXISTS (
                  SELECT 1 
                  FROM scholarship_applications sa 
                  WHERE sa.scholarship_id = s.scholarship_id 
                    AND sa.user_id = ? 
                    AND sa.status != 'CANCELLED'
              )
            ORDER BY s.scholarship_id DESC
            """;
        try {
            // Passes the active logged-in userId parameter straight into the SQL template hook
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            System.err.println("❌ REPO ERROR: findAvailablePrograms failed -> " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Resolves the primary student user ID tied to an email address.
     */
    public Integer findUserIdByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, email);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 🟢 FIXED: Checks previous application entries accurately across your column keys
     */
    public boolean hasAlreadyApplied(int userId, int scholarshipId) {
        String sql = "SELECT COUNT(*) FROM scholarship_applications WHERE user_id = ? AND scholarship_id = ? AND status != 'CANCELLED'";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, scholarshipId);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("❌ REPO ERROR -> hasAlreadyApplied check failed: " + e.getMessage());
            return false;
        }
    }

   
    /**
     * 🟢 FIXED: Automatically resolves and binds the student's active batch_id to prevent database insertion crashes!
     */
    public int insertApplication(int userId, int scholarshipId, String reason, String documentName) {
        // 1. First, dynamically fetch the student's active batch code ID from their enrollment entry row
        Integer activeBatchId = null;
        try {
            String batchLookupSql = "SELECT batch_id FROM enrollments WHERE user_id = ? AND LOWER(status) = 'active' LIMIT 1";
            activeBatchId = jdbcTemplate.queryForObject(batchLookupSql, Integer.class, userId);
        } catch (Exception e) {
            System.err.println("⚠️ REPO NOTICE -> No active enrollment batch found for userId " + userId + ". Trying fallback lookup...");
            try {
                // Second string fallback: Check any latest submission entry row regardless of string casing flags
                String fallbackBatchSql = "SELECT batch_id FROM enrollments WHERE user_id = ? ORDER BY enrollment_id DESC LIMIT 1";
                activeBatchId = jdbcTemplate.queryForObject(fallbackBatchSql, Integer.class, userId);
            } catch (Exception ex) {
                activeBatchId = null;
            }
        }

        // If no batch_id can be found, print a warning notice
        if (activeBatchId == null) {
            System.err.println("🚨 CRITICAL ERROR: Cannot submit application because student has no registered batch_id.");
            return 0;
        }

        // 2. 🟢 FIXED: Explicitly insert 'batch_id' into your projection block statement
        String sql = """
            INSERT INTO scholarship_applications (
                user_id, 
                scholarship_id, 
                batch_id, 
                reason, 
                supporting_document, 
                status, 
                created_at
            ) VALUES (?, ?, ?, ?, ?, 'PENDING', NOW())
            """;
        try {
            return jdbcTemplate.update(sql, userId, scholarshipId, activeBatchId, reason, documentName);
        } catch (Exception e) {
            System.err.println("❌ CRITICAL REPO ERROR -> insertApplication failed execution: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

}