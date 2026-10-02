package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Review {

    private Long reviewId;
    private int rating;
    private String comment;
    private StudentBean user;
    private CourseBean course;   
    private LocalDateTime createdAt;

    public Review() {
    }
}
