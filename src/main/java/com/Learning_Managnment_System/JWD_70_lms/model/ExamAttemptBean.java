package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data 
public class ExamAttemptBean {
    private Integer attempt_id;
    private Integer exam_id;
    private Integer enrollment_id;
    private Byte attempt_no;
    private BigDecimal score;
    private String result;
    private String status;
    private String remark;
    private Date submitted_at;

    // 🟢 FIXED: Named fields in snake_case to match the template expressions perfectly.
    // Lombok will generate getTitle(), getTotal_mark(), and getPass_mark() automatically!
    private String title; 
    private BigDecimal total_mark;
    private BigDecimal pass_mark;
}
