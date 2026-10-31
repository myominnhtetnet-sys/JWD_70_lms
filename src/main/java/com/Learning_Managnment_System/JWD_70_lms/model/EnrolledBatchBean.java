package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrolledBatchBean {
    private int batchId;
    private String title;
    private String scheduleNote;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

