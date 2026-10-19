package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentBean {
	private int paymentId;
    private Integer enrollmentId;
    private Integer scheduleId;
    private int paymentMethodId;
    private double amount;
    private String transactionNo;
    private String paymentType; 
    private String note;
    private String proofImage;
    private String paymentDate;
    private String status;
    private Integer processedBy;
    private String remark;
}
