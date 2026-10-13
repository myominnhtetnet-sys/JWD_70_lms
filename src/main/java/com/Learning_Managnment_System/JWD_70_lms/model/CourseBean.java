package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseBean {

    private Integer course_id;
    private Integer category_id;
    private String title;
    private String short_description;
    private String level;
    private String language;
    private Integer duration_weeks; 
    private Double price;
    private Integer allow_discount = 0;    
    private Integer allow_installment = 0;   
    private Integer allow_scholarship = 0; 

    public Integer getAllow_discount() { 
        return allow_discount != null ? allow_discount : 0; 
    }
    public void setAllow_discount(Object value) { 
        this.allow_discount = parseBooleanOrInt(value); 
    }

    public Integer getAllow_installment() { 
        return allow_installment != null ? allow_installment : 0; 
    }
    public void setAllow_installment(Object value) { 
        this.allow_installment = parseBooleanOrInt(value); 
    }

    public Integer getAllow_scholarship() { 
        return allow_scholarship != null ? allow_scholarship : 0; 
    }
    public void setAllow_scholarship(Object value) { 
        this.allow_scholarship = parseBooleanOrInt(value); 
    }

    private Integer parseBooleanOrInt(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value) ? 1 : 0;
        }
        return 0;
    }

    private LocalDateTime deleted_at;
    private String status;
    private String slug;
    private String thumbnail;
    private String description;
    private Integer created_by;
    private String category_name;
    private String created_by_name;

    // Sub-list for Batches
    private List<BatchBean> batches = new ArrayList<>();

    public CourseBean() {
    }
}