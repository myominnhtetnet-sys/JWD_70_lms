
package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalGradeBean {

    private Integer enrollmentId;
    private Integer userId;
    private String studentName;
    private String email;

    private BigDecimal assignmentPercent = BigDecimal.ZERO;
    private BigDecimal examPercent = BigDecimal.ZERO;
    private BigDecimal attendancePercent = BigDecimal.ZERO;

    private BigDecimal assignmentScore = BigDecimal.ZERO;
    private BigDecimal examScore = BigDecimal.ZERO;
    private BigDecimal totalScore = BigDecimal.ZERO;

    private String gradeLetter;
    private String result;
    private boolean published;

    
}
