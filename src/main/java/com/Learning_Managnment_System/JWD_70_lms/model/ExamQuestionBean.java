package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQuestionBean {

    private Long examId;
    private Long questionId;
    private String questionText;
    private String questionType;
    private BigDecimal mark;
    private Integer sortOrder;
    
   
}
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class ExamQuestionBean {
    private int questionId;
    private String questionText;
    private String questionType; // 'MCQ' or 'SHORT_ANSWER'
    private double questionMark;
    private List<QuestionOptionBean> options;
}
