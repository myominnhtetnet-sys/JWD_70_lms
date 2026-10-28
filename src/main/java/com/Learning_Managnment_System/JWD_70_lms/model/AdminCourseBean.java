package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminCourseBean {
	
	private Long courseId;
    private Long categoryId;
    private String categoryName; // Category table ကနေ Join ယူရန်
    private String title;
    private String slug;
    private String shortDescription;
    private String description;
    private String thumbnail;
    private String level;
    private String language;
    private BigDecimal price;
    private Integer durationWeeks;
    private String status;
    private Boolean allowDiscount;
    private Boolean allowInstallment;
    private Boolean allowScholarship;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
