package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AwardRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwardService {

    @Autowired
    private AwardRepository awardRepository;

    /**
     * Cleans up and trims date timestamps for the student awards list.
     */
    public List<StudentGradeBean> getStudentAwardsDashboard(String email) {
        List<StudentGradeBean> awardsList = awardRepository.findAwardsAndGradesByEmail(email);

        // Service layer isolates post-mapping formatting modifications cleanly
        for (StudentGradeBean grade : awardsList) {
            if (grade.getCertificate() != null) {
                String rawDate = grade.getCertificate().getIssuedAt();
                if (rawDate != null && rawDate.length() >= 10) {
                    grade.getCertificate().setIssuedAt(rawDate.substring(0, 10));
                }
            }
        }
        return awardsList;
    }

    /**
     * Cleans up and validates dates for a single specific certificate diploma.
     */
    public StudentGradeBean getPrintableCertificate(int certificateId, String email) {
        List<StudentGradeBean> matches = awardRepository.findCertificateDetailsByIdAndEmail(certificateId, email);
        if (matches.isEmpty()) {
            return null;
        }

        StudentGradeBean grade = matches.get(0);
        if (grade.getCertificate() != null) {
            String rawDate = grade.getCertificate().getIssuedAt();
            if (rawDate != null && rawDate.length() >= 10) {
                grade.getCertificate().setIssuedAt(rawDate.substring(0, 10));
            }
        }
        return grade;
    }
}
