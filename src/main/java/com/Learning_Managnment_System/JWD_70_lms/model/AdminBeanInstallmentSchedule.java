package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class AdminBeanInstallmentSchedule {
    
    private int scheduleId;
    private int enrollmentId;
    private int ruleItemId;
    private int installmentNumber;
    private String dueDate;
    private BigDecimal amountDue;
    private BigDecimal lateFee;
    private BigDecimal paidAmount;
    private String status;
    private String paidAt;
    private String reminderSentAt;
    private String createdAt;
    private String updatedAt;

}