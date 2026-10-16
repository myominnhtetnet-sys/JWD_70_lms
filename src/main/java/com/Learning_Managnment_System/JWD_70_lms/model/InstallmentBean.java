package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter

@Setter
public class InstallmentBean {
	private int scheduleId;
    private int enrollmentId;
    private int installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amountDue; // amount_due ကို ညွှန်းဆိုရန်
    private BigDecimal lateFee;
    private BigDecimal paidAmount;
    private String status;
}