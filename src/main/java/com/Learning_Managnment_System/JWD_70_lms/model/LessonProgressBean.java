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
    
    // 🟢 ADDED TO SUPPORT CONTENT VIEWS
    private String content;
    private String videoUrl;
    private String materialTitle;
    private String filePath;
    private String fileType;
}
