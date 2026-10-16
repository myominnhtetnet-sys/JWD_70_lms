package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentDetailBean {
    private int enrollmentId;
    private String studentName;
    private String studentEmail;
    private String courseTitle;
    private String batchCode;
    
    // Fee Breakdown (BigDecimal ဖြင့် သတ်မှတ်ရန်)
    private BigDecimal totalFee;
    private BigDecimal discountAmount;
    private BigDecimal scholarshipAmount;
    private BigDecimal finalAmount;
    
    private String paymentType;
    private String paymentStatus;
    private String enrollmentStatus;
    private LocalDate createdAt;

    private List<InstallmentBean> installments;
    private List<PaymentBean> payments;
}