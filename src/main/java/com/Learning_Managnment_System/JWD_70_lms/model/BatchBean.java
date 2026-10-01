package com.Learning_Managnment_System.JWD_70_lms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "batches", schema = "lms_db")
public class BatchBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "batch_code", nullable = false, unique = true)
    private String batchCode;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "schedule_note")
    private String scheduleNote;

    @Column(name = "max_seats", nullable = false)
    private Integer maxSeats;

    @Column(nullable = false)
    private String status;

    // Many batches belong to one course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseBean course;
}
