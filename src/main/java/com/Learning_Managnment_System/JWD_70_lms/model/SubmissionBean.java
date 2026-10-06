package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionBean {

    private Integer submissionId;
    private Integer assignmentId;
    private Integer enrollmentId;
    private Integer attemptNo;
    private String answerText;
    private String attachment;
    private LocalDateTime submittedAt;
    private Boolean isLate;
    private BigDecimal score;
    private String feedback;
    private Integer gradedBy;
    private LocalDateTime gradedAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //for student assign
    
    private String studentName;
    private String assignmentTitle;
    private BigDecimal totalMark;
}