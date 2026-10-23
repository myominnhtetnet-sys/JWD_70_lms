package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ScholarshipApplicationsBean {

    private Integer applicationId;
    private Integer scholarshipId;
    private Integer userId;
    private Integer batchId;
    private String reason;
    private String supportingDocument;
    private String status;
    private BigDecimal approvedAmount;
    private Integer reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    private String studentName;
    private String studentEmail;
    private String scholarshipName;

}