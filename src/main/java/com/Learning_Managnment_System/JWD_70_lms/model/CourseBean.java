package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
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
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "thumbnail")
    private String thumbnail;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

 // Inside your CourseBean.java file - Add this field mapping:

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("startDate ASC") // Automatically orders schedules by earliest start date
    private java.util.List<BatchBean> batches = new java.util.ArrayList<>();


    public CourseBean() {
    }
}
