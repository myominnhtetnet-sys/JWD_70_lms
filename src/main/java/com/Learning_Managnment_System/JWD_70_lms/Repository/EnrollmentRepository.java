package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentRepository {
	@Autowired
    private JdbcTemplate jdbcTemplate;

   
    public Map<String, Object> getCourseById(int courseId) {
        String sql = "SELECT course_id, title, price, allow_discount, allow_installment, allow_scholarship FROM courses WHERE course_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, courseId);
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<Map<String, Object>> getDiscountsByCourseId(int courseId) {
        String sql = """
            SELECT 
                dr.discount_rule_id, 
                dt.name AS discount_name, 
                dt.description,
                dr.min_amount
            FROM discount_rules dr
            JOIN discount_types dt ON dr.rule_type_code = dt.type_code
            WHERE dr.course_id = ? OR dr.course_id IS NULL
            """;
        return jdbcTemplate.queryForList(sql, courseId);
    }

   
    public List<Map<String, Object>> getInstallmentRules(int courseId) {
        String sql = "SELECT rule_id, name, installment_count, late_fee, grace_days FROM installment_rules WHERE course_id = ? AND is_active = 1";
        return jdbcTemplate.queryForList(sql, courseId);
    }
}
