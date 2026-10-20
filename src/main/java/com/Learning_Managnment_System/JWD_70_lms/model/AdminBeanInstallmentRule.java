package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class AdminBeanInstallmentRule {
    
    private int ruleId;
    private Integer courseId;
    private String name;
    private int installmentCount;
    private BigDecimal lateFee;
    private Integer graceDays;
    private String isActive;
    private Integer createdBy;
    private String createdAt;
    private String updatedAt;

}