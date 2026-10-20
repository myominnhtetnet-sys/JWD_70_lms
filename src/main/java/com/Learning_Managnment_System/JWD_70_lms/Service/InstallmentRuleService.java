package com.Learning_Managnment_System.JWD_70_lms.Service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleItemRespority;
import com.Learning_Managnment_System.JWD_70_lms.Repository.InstallmentRuleRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRule;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRuleItem;

@Service
public class InstallmentRuleService {

    private final InstallmentRuleRespority installmentRuleRepository;
    private final InstallmentRuleItemRespority installmentRuleItemRepository;

    public InstallmentRuleService(InstallmentRuleRespority installmentRuleRepository, 
                                  InstallmentRuleItemRespority installmentRuleItemRepository) {
        this.installmentRuleRepository = installmentRuleRepository;
        this.installmentRuleItemRepository = installmentRuleItemRepository;
    }

    // ==========================================
    // INSTALLMENT RULE METHODS
    // ==========================================

    public List<AdminBeanInstallmentRule> getAllInstallmentRules() {
        return installmentRuleRepository.getAllInstallmentRules();
    }

    public AdminBeanInstallmentRule createInstallmentRule(AdminBeanInstallmentRule rule) {
        return installmentRuleRepository.createInstallmentRule(rule);
    }

    public AdminBeanInstallmentRule getInstallmentRuleById(Integer id) {
        return installmentRuleRepository.getInstallmentRuleById(id);
    }

    public List<AdminBeanInstallmentRule> getInstallmentRulesByCourseId(Integer courseId) {
        return installmentRuleRepository.getRulesByCourseId(courseId);
    }

    public AdminBeanInstallmentRule updateInstallmentRule(Integer id, AdminBeanInstallmentRule rule) {
        return installmentRuleRepository.updateInstallmentRule(id, rule);
    }

    public boolean deleteInstallmentRule(Integer id) {
        return installmentRuleRepository.deleteInstallmentRule(id);
    }

    // ==========================================
    // INSTALLMENT RULE ITEM METHODS (Steps Breakdown)
    // ==========================================

    public List<AdminBeanInstallmentRuleItem> getRuleItemsByRuleId(Integer ruleId) {
        return installmentRuleItemRepository.getItemsByRuleId(ruleId);
    }

    public AdminBeanInstallmentRuleItem createInstallmentRuleItem(AdminBeanInstallmentRuleItem item) {
        return installmentRuleItemRepository.createInstallmentRuleItem(item);
    }

    public AdminBeanInstallmentRuleItem updateInstallmentRuleItem(Integer id, AdminBeanInstallmentRuleItem item) {
        return installmentRuleItemRepository.updateInstallmentRuleItem(id, item);
    }

    public boolean deleteInstallmentRuleItem(Integer id) {
        return installmentRuleItemRepository.deleteInstallmentRuleItem(id);
    }
}