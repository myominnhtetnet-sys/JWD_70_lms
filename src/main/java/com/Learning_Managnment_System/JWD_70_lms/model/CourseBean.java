package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseBean {

    private Integer course_id;
    private Integer category_id;
    private String title;
    private String short_description;
    private String level;
    private String language;
    private Integer duration_weeks; 
    private Double price;           
    private Integer allow_discount;    
    private Integer allow_installment;   
    private Integer allow_scholarship; 
    private LocalDateTime deleted_at;
    private String status;
    private String slug;
    private String thumbnail;
    private String description;

    // Plain Java List - No JPA annotations needed. Thymeleaf reads this directly.
    private List<BatchBean> batches = new ArrayList<>();

    public CourseBean() {
    }
}
