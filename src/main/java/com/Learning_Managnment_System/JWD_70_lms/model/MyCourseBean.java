package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCourseBean {
    private int courseId;
    private String courseTitle;
    private String batchCode;
    private String batchStatus;
    private String teacherName;
    private int completedLessons;
    private int totalLessons;
    private int progressPercent;

}
