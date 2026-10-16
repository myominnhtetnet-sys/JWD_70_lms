package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
	@Getter @Setter
    public static class BatchInfo {
        private Long batchId;
        private Long courseId;
        private String courseTitle; // Join ဖြင့် ဆွဲထုတ်ရန်
        private String batchCode;
        private String title;
        private String scheduleNote;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer maxSeats;
        private String status;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter @Setter
    public static class InstructorAssignment {
        private Long batchId;
        private Long instructorId;
        private String instructorName; // Join ဖြင့် ဆွဲထုတ်ရန်
        private LocalDateTime assignedAt;
    }
}
