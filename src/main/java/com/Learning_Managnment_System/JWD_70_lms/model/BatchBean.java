package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchBean {

    private Long batch_id;
    private Long course_id;
    private String batch_code;
    private String title;
    private LocalDate start_date;
    private LocalDate end_date;
    
    // 🟢 IDENTICAL TO SCHEMA: Changed from scheduleNote to schedule_note
    private String schedule_note; 
    
    private Integer max_seats;
    private String status;
    private Integer created_by;
}
