package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ScholarshipBean {

    private Integer scholarshipId;
    private Integer courseId; 
    private String name;
    private String description; 
    private String sponsor; 
    private String typeCode; 
    private BigDecimal coverageValue; 
    private Integer maxRecipients; 
    private LocalDate applicationStart; 
    private LocalDate applicationDeadline; 
    private Boolean isActive; 
    private Integer createdBy; 
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 
    private double discountPercentage;

   }