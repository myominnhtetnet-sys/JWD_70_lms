package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchBean {

    private Integer batchId;
    private String batchCode;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleNote;
    private Integer maxSeats;
    private String status;
    private Long courseId; // Maps direct relational key id reference directly
}
