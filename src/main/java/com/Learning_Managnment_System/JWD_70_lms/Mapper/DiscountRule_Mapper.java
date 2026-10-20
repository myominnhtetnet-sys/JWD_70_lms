package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.Learning_Managnment_System.JWD_70_lms.model.DiscountRuleBean;

@Mapper
public interface DiscountRule_Mapper {
    List<DiscountRuleBean> selectAllDiscountRules();
    int insertDiscountRule(DiscountRuleBean rule);
    int insertBatchDiscountRules(@Param("rules") List<DiscountRuleBean> rules);
    DiscountRuleBean selectDiscountRuleById(@Param("discountRuleId") Integer discountRuleId);
    List<DiscountRuleBean> selectRulesByDiscountId(@Param("discountId") Integer discountId);
    int updateDiscountRule(DiscountRuleBean rule);
    int deleteDiscountRule(@Param("discountRuleId") Integer discountRuleId);
    int deleteRulesByDiscountId(@Param("discountId") Integer discountId);
}