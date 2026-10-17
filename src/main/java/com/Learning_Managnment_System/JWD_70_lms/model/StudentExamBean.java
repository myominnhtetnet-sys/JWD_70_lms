
package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StudentExamBean {

    private Integer exam_id;
    private Integer course_id;
    private Integer batch_id;

    private String title;
    private String exam_type;

    private Short duration_min;

    private BigDecimal total_mark;
    private BigDecimal pass_mark;

    private Byte max_attempts;
}
