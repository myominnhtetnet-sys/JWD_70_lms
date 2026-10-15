package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamGradingForm {

    private List<AnswerMark> answers = new ArrayList<>();
    private String remark;

    @Getter
    @Setter
    public static class AnswerMark {

        private Long questionId;
        private BigDecimal mark;
    }
}

