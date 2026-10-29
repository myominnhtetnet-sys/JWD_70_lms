package com.Learning_Managnment_System.JWD_70_lms.Service;


import com.Learning_Managnment_System.JWD_70_lms.Repository.SettingsRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountRuleTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.DiscountTypeBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SettingsBean.PaymentMethodBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private SettingsRespority settingsRepository;

    // =========================================================================
    // 1. PAYMENT METHODS SERVICE METHODS
    // =========================================================================

    public List<PaymentMethodBean> getAllPaymentMethods() {
        return settingsRepository.getAllPaymentMethods();
    }

    public PaymentMethodBean getPaymentMethodById(int paymentMethodId) {
        return settingsRepository.getPaymentMethodById(paymentMethodId);
    }

    public int savePaymentMethod(PaymentMethodBean paymentMethod) {
        return settingsRepository.createPaymentMethod(paymentMethod);
    }

    public int updatePaymentMethod(int paymentMethodId, PaymentMethodBean paymentMethod) {
        return settingsRepository.updatePaymentMethod(paymentMethodId, paymentMethod);
    }

    public int deletePaymentMethod(int paymentMethodId) {
        return settingsRepository.deletePaymentMethod(paymentMethodId);
    }

    // =========================================================================
    // 2. DISCOUNT TYPES SERVICE METHODS
    // =========================================================================

    public List<DiscountTypeBean> getAllDiscountTypes() {
        return settingsRepository.getAllDiscountTypes();
    }

    public DiscountTypeBean getDiscountTypeByCode(String typeCode) {
        return settingsRepository.getDiscountTypeByCode(typeCode);
    }

    public int saveDiscountType(DiscountTypeBean discountType) {
        return settingsRepository.createDiscountType(discountType);
    }

    public int updateDiscountType(String typeCode, DiscountTypeBean discountType) {
        return settingsRepository.updateDiscountType(typeCode, discountType);
    }

    public int deleteDiscountType(String typeCode) {
        return settingsRepository.deleteDiscountType(typeCode);
    }

    // =========================================================================
    // 3. DISCOUNT RULE TYPES SERVICE METHODS
    // =========================================================================

    public List<DiscountRuleTypeBean> getAllDiscountRuleTypes() {
        return settingsRepository.getAllDiscountRuleTypes();
    }

    public DiscountRuleTypeBean getDiscountRuleTypeByCode(String ruleTypeCode) {
        return settingsRepository.getDiscountRuleTypeByCode(ruleTypeCode);
    }

    public int saveDiscountRuleType(DiscountRuleTypeBean ruleType) {
        return settingsRepository.createDiscountRuleType(ruleType);
    }

    public int updateDiscountRuleType(String ruleTypeCode, DiscountRuleTypeBean ruleType) {
        return settingsRepository.updateDiscountRuleType(ruleTypeCode, ruleType);
    }

    public int deleteDiscountRuleType(String ruleTypeCode) {
        return settingsRepository.deleteDiscountRuleType(ruleTypeCode);
    }
}