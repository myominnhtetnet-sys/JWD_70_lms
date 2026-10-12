package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamScheduleBean {
    private int examId;
    private String examTitle;
    private String courseTitle;
    private LocalDate examDate;
    private int durationMinutes;
}
