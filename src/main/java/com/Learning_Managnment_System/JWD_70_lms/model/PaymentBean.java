package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentBean {
    private int paymentId;
    private Integer enrollmentId;
    private Integer scheduleId;
    private int paymentMethodId;
    
    // Financial amount ကို precision မှန်ကန်စေရန် BigDecimal ဖြင့် သုံးထားပါသည်
    private BigDecimal amount;
    
    private String transactionNo;
    private String paymentType; 
    private String note;
    private String proofImage;
    
    // Date/Time အတွက် String အစား LocalDateTime ကို သုံးထားပါသည်
    private LocalDateTime paymentDate;
    
    private String status; // PENDING, SUCCESS, REJECTED, etc.
    private Integer processedBy;
    private String remark;
    
    // Additional fields for Joining & Display (Optional)
    private String paymentMethodName;
    private String studentName;
}