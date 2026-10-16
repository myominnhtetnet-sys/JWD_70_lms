package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity 
@Table(name = "courses", schema = "lms_db")
public class CourseBean {

    @Id 
    @Column(name = "course_id")
    private Long course_id;
    
    private Long category_id;
    private String title;
    private String slug;
    private String short_description;
    private String level;
    private String language;
    private Integer duration_weeks; // Changed from int to Integer
    private Double price;           // Changed from double to Double

    private Integer allow_discount;    // Changed from int to Integer
    private Integer allow_installment;   // Changed from int to Integer
    private Integer allow_scholarship; // Changed from int to Integer

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @Column(name = "status")
    private String status;


    public CourseBean() {
    }
}
