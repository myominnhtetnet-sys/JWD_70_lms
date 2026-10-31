package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateBean {

    private Integer certificateId;
    private Integer enrollmentId;

    private String certificateCode;
    private String filePath;

    private LocalDateTime issuedAt;

    private Integer issuedBy;
    private String status; // ISSUED, REVOKED
    private String revokedReason;

    private LocalDateTime createdAt;

    private String studentName;
    private String studentEmail;
    private String batchCode;
    private String courseTitle;
    private String issuerName;
}