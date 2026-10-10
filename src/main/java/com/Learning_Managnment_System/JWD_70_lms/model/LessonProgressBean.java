package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LessonProgressBean {
    private int lessonId;
    private String moduleTitle;
    private String lessonTitle;
    private String contentType;
    private int durationMin;
    private boolean isDone;
}
