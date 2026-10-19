package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter

@Setter
public class InstallmentBean {
    private int schedule_id;
    private int enrollment_id;
    private int rule_item_id;
    private int installment_number;
    private LocalDate due_date;
    private BigDecimal amount_due;
    private BigDecimal late_fee;
    private BigDecimal paid_amount;
    private String status;
    private LocalDateTime paid_at;
    private LocalDateTime reminder_sent_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    // Additional fields for Joining
    private String student_name;
    private String student_email;
    private String course_name;
    private String batch_name;
    private int days_overdue;
    private String overdue_status;

}