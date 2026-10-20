package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRule;


@Mapper
public interface InstallmentRuleMapper {
    List<AdminBeanInstallmentRule> selectAllInstallmentRules();
    int insertInstallmentRule(AdminBeanInstallmentRule rule);
    int insertBatchInstallmentRules(@Param("rules") List<AdminBeanInstallmentRule> rules);
    AdminBeanInstallmentRule selectInstallmentRuleById(@Param("ruleId") Integer ruleId);
    List<AdminBeanInstallmentRule> selectRulesByCourseId(@Param("courseId") Integer courseId);
    int updateInstallmentRule(AdminBeanInstallmentRule rule);
    int deleteInstallmentRule(@Param("ruleId") Integer ruleId);
    int deleteRulesByCourseId(@Param("courseId") Integer courseId);
}