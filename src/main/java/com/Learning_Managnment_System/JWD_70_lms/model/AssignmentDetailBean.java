package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDetailBean {

    private Integer assignmentId;

    private String title;
    private String description;

    private String batchName;
    private String lessonName;

    private String attachment;

    private LocalDateTime startAt;
    private LocalDateTime dueAt;

    private Boolean allowLateSubmit;
    private LocalDateTime lateDeadline;

    private BigDecimal totalMark;
    private BigDecimal passMark;

    private String status;
}