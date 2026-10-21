package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CertificateBean {
    private Integer certificateId; 
    private String certificateCode;
    private String issuedAt;
    private String certStatus;
    private String studentName;
	
}
