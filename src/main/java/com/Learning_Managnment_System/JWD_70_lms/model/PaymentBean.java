package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentBean {
    private int paymentId;
    private int enrollmentId;
    private double paidAmount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String referenceNo;
    private String status; // PENDING, APPROVED, REJECTED
}