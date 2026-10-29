package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AwardRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;

@Service
public class AwardService {

    private final AwardRepository awardRepository;

    public AwardService(AwardRepository awardRepository) {
        this.awardRepository = awardRepository;
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Get student awards and grades for dashboard.
     */
    public List<StudentGradeBean> getStudentAwardsDashboard(String email) {

        return awardRepository.findAwardsAndGradesByEmail(email);
    }

    /**
     * Get details for a single printable certificate.
     */
    public StudentGradeBean getPrintableCertificate(
            int certificateId, String email) {

        List<StudentGradeBean> matches =
                awardRepository.findCertificateDetailsByIdAndEmail(
                        certificateId, email);

        if (matches.isEmpty()) {
            return null;
        }

        return matches.get(0);
    }

    /**
     * Format LocalDateTime as yyyy-MM-dd for display.
     */
    public String formatIssuedDate(CertificateBean certificate) {

        if (certificate == null || certificate.getIssuedAt() == null) {
            return "";
        }

        LocalDateTime issuedAt = certificate.getIssuedAt();

        return issuedAt.format(DATE_FORMATTER);
    }
}