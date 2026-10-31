package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ScholarshipApplicationsMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ScholarshipApplicationsRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Fetch all scholarship applications with joined student and scholarship program details.
     */
    public List<ScholarshipApplicationsBean> getAllApplications() {
        String sql = "SELECT sa.*, " +
                     "u.full_name AS student_name, u.email AS student_email, " + 
                     "p.name AS scholarship_name " + 
                     "FROM scholarship_applications sa " +
                     "LEFT JOIN users u ON sa.user_id = u.user_id " +
                     "LEFT JOIN scholarships p ON sa.scholarship_id = p.scholarship_id " +
                     "ORDER BY sa.application_id DESC";
        
        return jdbcTemplate.query(sql, new ScholarshipApplicationsMapper());
    }

    /**
     * Convenience alias method for controllers/services expecting findAll().
     */
    public List<ScholarshipApplicationsBean> findAll() {
        return getAllApplications();
    }

    /**
     * Fetch a single scholarship application by its ID with joined student and program details.
     */
    public ScholarshipApplicationsBean getApplicationById(int applicationId) {
        String sql = "SELECT sa.*, " +
                     "u.full_name AS student_name, u.email AS student_email, " +
                     "p.name AS scholarship_name " + 
                     "FROM scholarship_applications sa " +
                     "LEFT JOIN users u ON sa.user_id = u.user_id " +
                     "LEFT JOIN scholarships p ON sa.scholarship_id = p.scholarship_id " +
                     "WHERE sa.application_id = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, new ScholarshipApplicationsMapper(), applicationId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Fetch a single scholarship application by its ID (Long version).
     */
    public ScholarshipApplicationsBean findById(Long applicationId) {
        if (applicationId == null) {
            return null;
        }
        return getApplicationById(applicationId.intValue());
    }

    /**
     * Review and update scholarship application status, amount, and notes.
     */
    public int reviewApplication(int applicationId, String status, BigDecimal approvedAmount, int reviewedBy, String reviewNotes) {
        String sql = "UPDATE scholarship_applications " +
                     "SET status = ?, approved_amount = ?, reviewed_by = ?, reviewed_at = NOW(), review_notes = ?, updated_at = NOW() " +
                     "WHERE application_id = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setBigDecimal(2, approvedAmount != null ? approvedAmount : BigDecimal.ZERO);
            ps.setInt(3, reviewedBy);
            ps.setString(4, reviewNotes);
            ps.setInt(5, applicationId);
            return ps;
        });
    }

    /**
     * Delete a scholarship application by ID.
     */
    public int deleteApplication(int applicationId) {
        String sql = "DELETE FROM scholarship_applications WHERE application_id = ?";
        return jdbcTemplate.update(sql, applicationId);
    }
}