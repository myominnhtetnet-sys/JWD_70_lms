package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class AdminBeanInstallmentRuleItem {
    
    private int ruleItemId;
    private int ruleId;
    private int installmentNumber;
    private BigDecimal percentOfFee;
    private int dueAfterDays;

}