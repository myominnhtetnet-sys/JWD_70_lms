package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentBean {

    private int enrollment_id;

    private int user_id;

    private int batch_id;

    private String student_name;

    private String student_email;

    private String course_name;

    private String batch_name;

    private BigDecimal original_amount = BigDecimal.ZERO;

    private BigDecimal discount_amount = BigDecimal.ZERO;

    private BigDecimal scholarship_amount = BigDecimal.ZERO;

    private BigDecimal final_amount = BigDecimal.ZERO;

    private String payment_type;

    private String payment_status;

    private String status;

    private String status_reason;

    private LocalDateTime enrolled_at;
}