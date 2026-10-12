package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScholarshipApplicationBean {
    private int applicationId;
    private String programName;
    private LocalDate appliedDate; 
    private double discountPercent;
    private String status;
}