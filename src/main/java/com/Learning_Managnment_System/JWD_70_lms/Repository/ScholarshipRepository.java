package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationBean;

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
}