package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class CourseReviewBean {
	
	private Long reviewId;
    private Long courseId;
    private String courseTitle;
    private Long userId;
    private String studentName;
    private String studentEmail;
    private Integer rating;
    private String comment;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
