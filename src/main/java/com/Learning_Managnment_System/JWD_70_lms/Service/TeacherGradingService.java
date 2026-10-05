
package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.TeacherGradingRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Service
public class TeacherGradingService {

    private final TeacherGradingRepository repository;

    public TeacherGradingService(TeacherGradingRepository repository) {
        this.repository = repository;
    }

    // Get all student submissions
    public List<SubmissionBean> getAllSubmissions() {
        return repository.findAllSubmissions();
    }

    // Grade student submission

public void gradeSubmission(
        Integer submissionId,
        BigDecimal score,
        String feedback) {

    // 1. Check score
    if (score == null || score.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException(
                "Mark must be 0 or greater!"
        );
    }

    // 2. Check feedback
    if (feedback == null || feedback.isBlank()) {
        throw new IllegalArgumentException(
                "Feedback cannot be empty!"
        );
    }

    // 3. Get submission information
    SubmissionBean submission = getSubmissionById(submissionId);

    // 4. Check total mark
    BigDecimal totalMark = submission.getTotalMark();

    if (totalMark == null) {
        throw new IllegalArgumentException(
                "Assignment total mark is missing!"
        );
    }

    // 5. Prevent mark greater than total mark
    if (score.compareTo(totalMark) > 0) {
        throw new IllegalArgumentException(
                "Mark cannot be greater than total mark (" + totalMark + ")!"
        );
    }

    // Temporary Teacher ID
    Integer gradedBy = 1;

    // 6. Save grade
    int result = repository.gradeSubmission(
            submissionId,
            score,
            feedback,
            gradedBy
    );

    if (result == 0) {
        throw new IllegalArgumentException(
                "Submission not found!"
        );
    }
}
    public SubmissionBean getSubmissionById(Integer submissionId) {

        return repository.findSubmissionById(submissionId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Submission not found!")
                );
    }
}