package com.Learning_Managnment_System.JWD_70_lms.Repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRuleItem;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class InstallmentRuleItemRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminBeanInstallmentRuleItem> itemRowMapper = (rs, rowNum) -> {
        AdminBeanInstallmentRuleItem item = new AdminBeanInstallmentRuleItem();
        item.setRuleItemId(rs.getInt("rule_item_id"));
        item.setRuleId(rs.getInt("rule_id"));
        item.setInstallmentNumber(rs.getInt("installment_number"));
        item.setPercentOfFee(rs.getBigDecimal("percent_of_fee"));
        item.setDueAfterDays(rs.getInt("due_after_days"));
        return item;
    };

    public List<AdminBeanInstallmentRuleItem> getItemsByRuleId(int ruleId) {
        String sql = "SELECT rule_item_id, rule_id, installment_number, percent_of_fee, due_after_days " +
                     "FROM installment_rule_items WHERE rule_id = ? ORDER BY installment_number ASC";
        return jdbcTemplate.query(sql, itemRowMapper, ruleId);
    }

    public AdminBeanInstallmentRuleItem getInstallmentRuleItemById(int ruleItemId) {
        String sql = "SELECT rule_item_id, rule_id, installment_number, percent_of_fee, due_after_days " +
                     "FROM installment_rule_items WHERE rule_item_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, itemRowMapper, ruleItemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AdminBeanInstallmentRuleItem createInstallmentRuleItem(AdminBeanInstallmentRuleItem item) {
        String sql = "INSERT INTO installment_rule_items (rule_id, installment_number, percent_of_fee, due_after_days) " +
                     "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, item.getRuleId());
            ps.setInt(2, item.getInstallmentNumber());
            ps.setBigDecimal(3, item.getPercentOfFee());
            ps.setInt(4, item.getDueAfterDays());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            int newId = keyHolder.getKey().intValue();
            return getInstallmentRuleItemById(newId);
        }
        return null;
    }

    public AdminBeanInstallmentRuleItem updateInstallmentRuleItem(int id, AdminBeanInstallmentRuleItem item) {
        String sql = "UPDATE installment_rule_items SET rule_id = ?, installment_number = ?, " +
                     "percent_of_fee = ?, due_after_days = ? WHERE rule_item_id = ?";

        int rows = jdbcTemplate.update(sql,
                item.getRuleId(),
                item.getInstallmentNumber(),
                item.getPercentOfFee(),
                item.getDueAfterDays(),
                id
        );

        return rows > 0 ? getInstallmentRuleItemById(id) : null;
    }

    public boolean deleteInstallmentRuleItem(int ruleItemId) {
        String sql = "DELETE FROM installment_rule_items WHERE rule_item_id = ?";
        return jdbcTemplate.update(sql, ruleItemId) > 0;
    }
}