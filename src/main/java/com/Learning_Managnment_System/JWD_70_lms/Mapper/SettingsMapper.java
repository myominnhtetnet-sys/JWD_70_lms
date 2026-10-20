package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountRuleTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.PaymentMethodBean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsMapper {

    public static class PaymentMethodMapper implements RowMapper<PaymentMethodBean> {
        @Override
        public PaymentMethodBean mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaymentMethodBean paymentMethod = new PaymentMethodBean();
            paymentMethod.setPaymentMethodId(rs.getInt("payment_method_id"));
            paymentMethod.setName(rs.getString("name"));
            paymentMethod.setDescription(rs.getString("description"));
            paymentMethod.setIsActive(rs.getInt("is_active"));

            if (rs.getTimestamp("created_at") != null) {
                paymentMethod.setCreatedAt(rs.getTimestamp("created_at"));
            }
            return paymentMethod;
        }
    }

    public static class DiscountTypeMapper implements RowMapper<DiscountTypeBean> {
        @Override
        public DiscountTypeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
            DiscountTypeBean discountType = new DiscountTypeBean();
            discountType.setTypeCode(rs.getString("type_code"));
            discountType.setName(rs.getString("name"));
            discountType.setDescription(rs.getString("description"));
            return discountType;
        }
    }

    public static class DiscountRuleTypeMapper implements RowMapper<DiscountRuleTypeBean> {
        @Override
        public DiscountRuleTypeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
            DiscountRuleTypeBean ruleType = new DiscountRuleTypeBean();
            ruleType.setRuleTypeCode(rs.getString("rule_type_code"));
            ruleType.setName(rs.getString("name"));
            ruleType.setAppliesTo(rs.getString("applies_to"));
            ruleType.setDescription(rs.getString("description"));
            return ruleType;
        }
    }
}