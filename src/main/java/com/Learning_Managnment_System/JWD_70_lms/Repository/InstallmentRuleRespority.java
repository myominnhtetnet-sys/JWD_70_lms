package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class InstallmentRuleRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminBeanInstallmentRule> installmentRuleRowMapper = (rs, rowNum) -> {
        AdminBeanInstallmentRule rule = new AdminBeanInstallmentRule();
        rule.setRuleId(rs.getInt("rule_id"));
        rule.setCourseId(rs.getObject("course_id") != null ? rs.getInt("course_id") : null);
        rule.setName(rs.getString("name"));
        rule.setInstallmentCount(rs.getInt("installment_count"));
        rule.setLateFee(rs.getBigDecimal("late_fee"));
        rule.setGraceDays(rs.getObject("grace_days") != null ? rs.getInt("grace_days") : null);
        rule.setIsActive(rs.getString("is_active"));
        rule.setCreatedBy(rs.getObject("created_by") != null ? rs.getInt("created_by") : null);

        Timestamp createdAt = rs.getTimestamp("created_at");
        rule.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime().toString() : null);

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        rule.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime().toString() : null);

        return rule;
    };

    public List<AdminBeanInstallmentRule> getAllInstallmentRules() {
        String sql = "SELECT rule_id, course_id, name, installment_count, late_fee, grace_days, " +
                     "is_active, created_by, created_at, updated_at " +
                     "FROM installment_rules ORDER BY rule_id DESC";
        return jdbcTemplate.query(sql, installmentRuleRowMapper);
    }

    public AdminBeanInstallmentRule getInstallmentRuleById(int ruleId) {
        String sql = "SELECT rule_id, course_id, name, installment_count, late_fee, grace_days, " +
                     "is_active, created_by, created_at, updated_at " +
                     "FROM installment_rules WHERE rule_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, installmentRuleRowMapper, ruleId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AdminBeanInstallmentRule> getRulesByCourseId(int courseId) {
        String sql = "SELECT rule_id, course_id, name, installment_count, late_fee, grace_days, " +
                     "is_active, created_by, created_at, updated_at " +
                     "FROM installment_rules WHERE course_id = ? ORDER BY rule_id DESC";
        return jdbcTemplate.query(sql, installmentRuleRowMapper, courseId);
    }

    public AdminBeanInstallmentRule createInstallmentRule(AdminBeanInstallmentRule rule) {
        String sql = "INSERT INTO installment_rules (course_id, name, installment_count, late_fee, " +
                     "grace_days, is_active, created_by, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            if (rule.getCourseId() != null) {
                ps.setInt(1, rule.getCourseId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ps.setString(2, rule.getName());
            ps.setInt(3, rule.getInstallmentCount());
            ps.setBigDecimal(4, rule.getLateFee());
            
            if (rule.getGraceDays() != null) {
                ps.setInt(5, rule.getGraceDays());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setString(6, rule.getIsActive() != null ? rule.getIsActive() : "1");

            int createdBy = (rule.getCreatedBy() != null) ? rule.getCreatedBy() : 1;
            ps.setInt(7, createdBy);

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            int newId = keyHolder.getKey().intValue();
            return getInstallmentRuleById(newId);
        }
        return null;
    }

    public AdminBeanInstallmentRule updateInstallmentRule(int id, AdminBeanInstallmentRule rule) {
        String sql = "UPDATE installment_rules SET course_id = ?, name = ?, installment_count = ?, " +
                     "late_fee = ?, grace_days = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE rule_id = ?";

        int rows = jdbcTemplate.update(sql,
                rule.getCourseId(),
                rule.getName(),
                rule.getInstallmentCount(),
                rule.getLateFee(),
                rule.getGraceDays(),
                rule.getIsActive() != null ? rule.getIsActive() : "1",
                id
        );

        return rows > 0 ? getInstallmentRuleById(id) : null;
    }

    public boolean deleteInstallmentRule(int ruleId) {
        String sql = "DELETE FROM installment_rules WHERE rule_id = ?";
        return jdbcTemplate.update(sql, ruleId) > 0;
    }
}