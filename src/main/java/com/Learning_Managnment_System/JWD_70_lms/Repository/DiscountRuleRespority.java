
package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.DiscountRuleBean;

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
import java.sql.Types;
import java.util.List;

@Repository
public class DiscountRuleRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Map database columns to DiscountRuleBean
    private final RowMapper<DiscountRuleBean> ruleRowMapper =
            (rs, rowNum) -> {

        DiscountRuleBean rule = new DiscountRuleBean();

        rule.setDiscountRuleId(rs.getInt("discount_rule_id"));

        rule.setDiscountId(rs.getInt("discount_id"));

        rule.setRuleTypeCode(rs.getString("rule_type_code"));

        rule.setCourseId(
                rs.getObject("course_id") != null
                        ? rs.getInt("course_id") : null
        );

        rule.setCategoryId(
                rs.getObject("category_id") != null
                        ? rs.getInt("category_id") : null
        );

        rule.setBatchId(
                rs.getObject("batch_id") != null
                        ? rs.getInt("batch_id") : null
        );

        rule.setUserId(
                rs.getObject("user_id") != null
                        ? rs.getInt("user_id") : null
        );

        rule.setMinAmount(rs.getBigDecimal("min_amount"));

        rule.setDaysBeforeStart(
                rs.getObject("days_before_start") != null
                        ? rs.getInt("days_before_start") : null
        );

        Timestamp createdAt = rs.getTimestamp("created_at");

        rule.setCreatedAt(
                createdAt != null
                        ? createdAt.toLocalDateTime() : null
        );

        return rule;
    };

    // Check whether the parent discount exists
    public boolean discountExists(Integer discountId) {

        if (discountId == null || discountId <= 0) {
            return false;
        }

        String sql =
                "SELECT COUNT(*) FROM discounts WHERE discount_id = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class, discountId
        );

        return count != null && count > 0;
    }

    // Get all discount rules
    public List<DiscountRuleBean> getAllDiscountRules() {

        String sql =
                "SELECT discount_rule_id, discount_id, rule_type_code, " +
                "course_id, category_id, batch_id, user_id, min_amount, " +
                "days_before_start, created_at " +
                "FROM discount_rules ORDER BY discount_rule_id DESC";

        return jdbcTemplate.query(sql, ruleRowMapper);
    }

    // Get a discount rule by its ID
    public DiscountRuleBean getDiscountRuleById(int id) {

        String sql =
                "SELECT discount_rule_id, discount_id, rule_type_code, " +
                "course_id, category_id, batch_id, user_id, min_amount, " +
                "days_before_start, created_at " +
                "FROM discount_rules WHERE discount_rule_id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql, ruleRowMapper, id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Get all rules belonging to one discount
    public List<DiscountRuleBean> getRulesByDiscountId(int discountId) {

        if (!discountExists(discountId)) {
            return List.of();
        }

        String sql =
                "SELECT discount_rule_id, discount_id, rule_type_code, " +
                "course_id, category_id, batch_id, user_id, min_amount, " +
                "days_before_start, created_at " +
                "FROM discount_rules " +
                "WHERE discount_id = ? ORDER BY discount_rule_id ASC";

        return jdbcTemplate.query(
                sql, ruleRowMapper, discountId
        );
    }

    // Create a discount rule
    public DiscountRuleBean createDiscountRule(
            DiscountRuleBean rule) {

        if (rule == null) {
            throw new IllegalArgumentException(
                    "Discount rule cannot be empty."
            );
        }

        if (!discountExists(rule.getDiscountId())) {
            throw new IllegalArgumentException(
                    "Invalid discountId. Please select an existing discount."
            );
        }

        if (rule.getRuleTypeCode() == null
                || rule.getRuleTypeCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Rule type is required."
            );
        }

        String sql =
                "INSERT INTO discount_rules " +
                "(discount_id, rule_type_code, course_id, category_id, " +
                "batch_id, user_id, min_amount, days_before_start, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, rule.getDiscountId());
            ps.setString(2, rule.getRuleTypeCode());

            setNullableInt(ps, 3, rule.getCourseId());
            setNullableInt(ps, 4, rule.getCategoryId());
            setNullableInt(ps, 5, rule.getBatchId());
            setNullableInt(ps, 6, rule.getUserId());

            if (rule.getMinAmount() != null) {
                ps.setBigDecimal(7, rule.getMinAmount());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            setNullableInt(ps, 8, rule.getDaysBeforeStart());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException(
                    "The rule was inserted, but its generated ID was not returned."
            );
        }

        return getDiscountRuleById(
                keyHolder.getKey().intValue()
        );
    }

    // Update an existing discount rule
    public DiscountRuleBean updateDiscountRule(
            int id, DiscountRuleBean rule) {

        if (rule == null) {
            throw new IllegalArgumentException(
                    "Discount rule cannot be empty."
            );
        }

        if (!discountExists(rule.getDiscountId())) {
            throw new IllegalArgumentException(
                    "Invalid discountId. Please select an existing discount."
            );
        }

        if (rule.getRuleTypeCode() == null
                || rule.getRuleTypeCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Rule type is required."
            );
        }

        String sql =
                "UPDATE discount_rules SET " +
                "discount_id = ?, rule_type_code = ?, course_id = ?, " +
                "category_id = ?, batch_id = ?, user_id = ?, " +
                "min_amount = ?, days_before_start = ? " +
                "WHERE discount_rule_id = ?";

        int rows = jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, rule.getDiscountId());
            ps.setString(2, rule.getRuleTypeCode());

            setNullableInt(ps, 3, rule.getCourseId());
            setNullableInt(ps, 4, rule.getCategoryId());
            setNullableInt(ps, 5, rule.getBatchId());
            setNullableInt(ps, 6, rule.getUserId());

            if (rule.getMinAmount() != null) {
                ps.setBigDecimal(7, rule.getMinAmount());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            setNullableInt(ps, 8, rule.getDaysBeforeStart());

            ps.setInt(9, id);

            return ps;
        });

        return rows > 0 ? getDiscountRuleById(id) : null;
    }

   
    
    public boolean deleteItemsByRuleId(int ruleId) {
        String sql = "DELETE FROM installment_rule_items WHERE rule_id = ?";
        return jdbcTemplate.update(sql, ruleId) > 0;
    }

    // Set an integer parameter, including SQL NULL
    private void setNullableInt(
            PreparedStatement ps,
            int paramIndex,
            Integer value) throws java.sql.SQLException {

        if (value != null) {
            ps.setInt(paramIndex, value);
        } else {
            ps.setNull(paramIndex, Types.INTEGER);
        }
    }
}