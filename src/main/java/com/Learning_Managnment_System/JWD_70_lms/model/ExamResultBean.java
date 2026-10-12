package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExamResultBean {
    private String title;
    private double score;
    private double totalMark;
    private double passMark;
    private String status;
    private String result;
}
