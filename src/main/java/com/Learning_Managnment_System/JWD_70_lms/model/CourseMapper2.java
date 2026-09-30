package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity 
@Table(name = "courses", schema = "lms_db") 
public class CourseMapper2 {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "course_id") // Maps Java field to SQL column precisely
    private Integer course_id;
    
    @Column(name = "category_id")
    private Integer category_id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "slug")
    private String slug;
    
    @Column(name = "short_description")
    private String short_description;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "thumbnail")
    private String thumbnail;
    
    @Column(name = "level")
    private String level;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "duration_weeks")
    private Integer duration_weeks; // Changed to wrapper Integer
    
    @Column(name = "allow_discount")
    private Integer allow_discount; // Changed to wrapper Integer
    
    @Column(name = "allow_installment")
    private Integer allow_installment; // Changed to wrapper Integer
    
    @Column(name = "allow_scholarship")
    private Integer allow_scholarship; // Changed to wrapper Integer
    
    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
