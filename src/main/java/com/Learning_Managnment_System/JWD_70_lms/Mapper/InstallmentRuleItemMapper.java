package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBeanInstallmentRuleItem;

@Mapper
public interface InstallmentRuleItemMapper {
    List<AdminBeanInstallmentRuleItem> selectItemsByRuleId(@Param("ruleId") Integer ruleId);
    int insertInstallmentRuleItem(AdminBeanInstallmentRuleItem item);
    int insertBatchInstallmentRuleItems(@Param("items") List<AdminBeanInstallmentRuleItem> items);
    AdminBeanInstallmentRuleItem selectInstallmentRuleItemById(@Param("ruleItemId") Integer ruleItemId);
    int updateInstallmentRuleItem(AdminBeanInstallmentRuleItem item);
    int deleteInstallmentRuleItem(@Param("ruleItemId") Integer ruleItemId);
    int deleteItemsByRuleId(@Param("ruleId") Integer ruleId);
}