package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallmentBean {
	
    private int scheduleId;
    private int enrollmentId;
    private int ruleItemId;
    private int installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amountDue;
    private BigDecimal lateFee;
    private BigDecimal paidAmount;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime reminderSentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for Joining & Display
    private String studentName;
    private String studentEmail;
    private String courseName;
    private String batchName;
    private int daysOverdue;
    private String overdueStatus;
}