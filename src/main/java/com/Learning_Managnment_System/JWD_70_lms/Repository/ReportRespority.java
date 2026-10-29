package com.Learning_Managnment_System.JWD_70_lms.Repository;


import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.ReportBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReportRespority {

    // In-memory data store
    private final List<ReportBean> reportList = new ArrayList<>();
    
    // Auto-increment ID generator starting after existing sample IDs
    private final AtomicLong idCounter = new AtomicLong(502);

    public ReportRespority() {
        // Pre-populate with initial dummy records matching your dashboard view
        ReportBean r1 = new ReportBean();
        r1.setReportId(501L);
        r1.setReportTitle("Q3 Scholarship Disbursement Summary");
        r1.setCategory("Financial & Aid");
        r1.setFormat("PDF");
        r1.setFileName("q3_scholarship.pdf");
        r1.setOriginalFileName("Q3_Scholarship_Disbursement_Summary.pdf");
        r1.setFileSize(245760L); // e.g. ~240 KB
        r1.setCreatedAt(LocalDateTime.now().minusDays(2));
        reportList.add(r1);

        ReportBean r2 = new ReportBean();
        r2.setReportId(502L);
        r2.setReportTitle("JWD_70 Course Attendance Audit");
        r2.setCategory("Student Attendance");
        r2.setFormat("CSV");
        r2.setFileName("jwd_70_audit.csv");
        r2.setOriginalFileName("JWD_70_Course_Attendance_Audit.csv");
        r2.setFileSize(51200L); // e.g. ~50 KB
        r2.setCreatedAt(LocalDateTime.now().minusDays(4));
        reportList.add(r2);
    }

  
    public List<ReportBean> findAll() {
        List<ReportBean> sortedList = new ArrayList<>(reportList);
        sortedList.sort((a, b) -> {
            if (b.getCreatedAt() == null || a.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        return sortedList;
    }

  
    public void save(ReportBean report) {
        if (report.getReportId() == null) {
            report.setReportId(idCounter.incrementAndGet());
        }
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(LocalDateTime.now());
        }
     
        reportList.add(0, report);
    }


    public ReportBean findById(Long reportId) {
        return reportList.stream()
                .filter(report -> report.getReportId().equals(reportId))
                .findFirst()
                .orElse(null);
    }
}