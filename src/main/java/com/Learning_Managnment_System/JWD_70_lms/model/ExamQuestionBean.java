package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQuestionBean {

    private Long examId;
    private Long questionId;
    private BigDecimal mark;
    private Integer sortOrder;
}