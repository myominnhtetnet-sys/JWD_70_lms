package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamBean {

    private Long examId;
    private Long courseId;
    private Long batchId;
    private String title;
    private String examType;
    private Integer durationMin;
    private BigDecimal totalMark;
    private BigDecimal passMark;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer maxAttempts;
    private Boolean shuffleQuestions;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}