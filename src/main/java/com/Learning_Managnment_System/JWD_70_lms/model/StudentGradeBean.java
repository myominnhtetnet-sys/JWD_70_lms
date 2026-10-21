package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentGradeBean {
    private int gradeId;
    private int enrollmentId;
    private String courseTitle;
    private double assignmentScore;
    private double examScore;
    private double attendancePercent;
    private double totalScore;
    private String gradeLetter;
    private String result;
    
    // 🟢 OBJECT COMPOSITION: Holds a reference to the child data model bean
    private CertificateBean certificate;
}
