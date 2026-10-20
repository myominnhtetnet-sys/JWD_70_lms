package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.SettingsMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountRuleTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.PaymentMethodBean;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SettingsRespority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================================
    // 1. PAYMENT METHODS CRUD
    // =========================================================================
    
    public List<PaymentMethodBean> getAllPaymentMethods() {
        String sql = "SELECT payment_method_id, name, description, is_active, created_at " +
                     "FROM payment_methods ORDER BY payment_method_id DESC";
        return jdbcTemplate.query(sql, new SettingsMapper.PaymentMethodMapper());
    }

    public PaymentMethodBean getPaymentMethodById(int paymentMethodId) {
        String sql = "SELECT payment_method_id, name, description, is_active, created_at " +
                     "FROM payment_methods WHERE payment_method_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new SettingsMapper.PaymentMethodMapper(), paymentMethodId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int createPaymentMethod(PaymentMethodBean paymentMethod) {
        String sql = "INSERT INTO payment_methods (name, description, is_active, created_at) " +
                     "VALUES (?, ?, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, paymentMethod.getName());
            ps.setString(2, paymentMethod.getDescription());
            ps.setInt(3, paymentMethod.getIsActive());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public int updatePaymentMethod(int paymentMethodId, PaymentMethodBean paymentMethod) {
        String sql = "UPDATE payment_methods SET name = ?, description = ?, is_active = ? " +
                     "WHERE payment_method_id = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, paymentMethod.getName());
            ps.setString(2, paymentMethod.getDescription());
            ps.setInt(3, paymentMethod.getIsActive());
            ps.setInt(4, paymentMethodId);
            return ps;
        });
    }

    public int deletePaymentMethod(int paymentMethodId) {
        String sql = "DELETE FROM payment_methods WHERE payment_method_id = ?";
        return jdbcTemplate.update(sql, paymentMethodId);
    }


    // =========================================================================
    // 2. DISCOUNT TYPES CRUD
    // =========================================================================

    public List<DiscountTypeBean> getAllDiscountTypes() {
        String sql = "SELECT type_code, name, description FROM discount_types ORDER BY type_code";
        return jdbcTemplate.query(sql, new SettingsMapper.DiscountTypeMapper());
    }

    public DiscountTypeBean getDiscountTypeByCode(String typeCode) {
        String sql = "SELECT type_code, name, description FROM discount_types WHERE type_code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new SettingsMapper.DiscountTypeMapper(), typeCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int createDiscountType(DiscountTypeBean discountType) {
        String sql = "INSERT INTO discount_types (type_code, name, description) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, 
            discountType.getTypeCode(), 
            discountType.getName(), 
            discountType.getDescription()
        );
    }

    public int updateDiscountType(String typeCode, DiscountTypeBean discountType) {
        String sql = "UPDATE discount_types SET name = ?, description = ? WHERE type_code = ?";
        return jdbcTemplate.update(sql, 
            discountType.getName(), 
            discountType.getDescription(), 
            typeCode
        );
    }

    public int deleteDiscountType(String typeCode) {
        String sql = "DELETE FROM discount_types WHERE type_code = ?";
        return jdbcTemplate.update(sql, typeCode);
    }


    // =========================================================================
    // 3. DISCOUNT RULE TYPES CRUD
    // =========================================================================

    public List<DiscountRuleTypeBean> getAllDiscountRuleTypes() {
        String sql = "SELECT rule_type_code, name, applies_to, description FROM discount_rule_types ORDER BY rule_type_code";
        return jdbcTemplate.query(sql, new SettingsMapper.DiscountRuleTypeMapper());
    }

    public DiscountRuleTypeBean getDiscountRuleTypeByCode(String ruleTypeCode) {
        String sql = "SELECT rule_type_code, name, applies_to, description FROM discount_rule_types WHERE rule_type_code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new SettingsMapper.DiscountRuleTypeMapper(), ruleTypeCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int createDiscountRuleType(DiscountRuleTypeBean ruleType) {
        String sql = "INSERT INTO discount_rule_types (rule_type_code, name, applies_to, description) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
            ruleType.getRuleTypeCode(), 
            ruleType.getName(), 
            ruleType.getAppliesTo(), 
            ruleType.getDescription()
        );
    }

    public int updateDiscountRuleType(String ruleTypeCode, DiscountRuleTypeBean ruleType) {
        String sql = "UPDATE discount_rule_types SET name = ?, applies_to = ?, description = ? WHERE rule_type_code = ?";
        return jdbcTemplate.update(sql, 
            ruleType.getName(), 
            ruleType.getAppliesTo(), 
            ruleType.getDescription(), 
            ruleTypeCode
        );
    }

    public int deleteDiscountRuleType(String ruleTypeCode) {
        String sql = "DELETE FROM discount_rule_types WHERE rule_type_code = ?";
        return jdbcTemplate.update(sql, ruleTypeCode);
    }
}