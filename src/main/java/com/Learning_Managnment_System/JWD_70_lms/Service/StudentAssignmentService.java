package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentAssignmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.SubmissionRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Service
public class StudentAssignmentService {

    private final StudentAssignmentRepository repository;
    private final SubmissionRepository submissionRepository;

    public StudentAssignmentService(
            StudentAssignmentRepository repository,
            SubmissionRepository submissionRepository) {

        this.repository = repository;
        this.submissionRepository = submissionRepository;
    }

    public List<AssignmentBean> getAllAssignments() {
        return repository.findAllAssignments();
    }

    public int submitAssignment(SubmissionBean submission) {

        // 1. Get Assignment Information
        AssignmentBean assignment = repository
                .findAssignmentById(submission.getAssignmentId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Assignment not found!")
                );

        // 2. Get Current Time
        LocalDateTime now = LocalDateTime.now();

        // 3. Check Due Date
        boolean isLate = now.isAfter(assignment.getDueAt());

        // 4. Check Late Submission Permission
        if (isLate) {

            if (!Boolean.TRUE.equals(assignment.getAllowLateSubmit())) {
                throw new IllegalArgumentException(
                        "Assignment deadline has passed. Late submission is not allowed."
                );
            }

            // Check Late Deadline
            if (assignment.getLateDeadline() == null
                    || now.isAfter(assignment.getLateDeadline())) {

                throw new IllegalArgumentException(
                        "Late submission deadline has passed!"
                );
            }
        }

        // 5. Prevent Duplicate Submission
        Optional<SubmissionBean> existingSubmission =
                submissionRepository.findByAssignmentAndEnrollment(
                        submission.getAssignmentId(),
                        submission.getEnrollmentId()
                );

        if (existingSubmission.isPresent()) {
            throw new IllegalArgumentException(
                    "You have already submitted this assignment!"
            );
        }

        // 6. Save Submission
        submission.setAttemptNo(1);
        submission.setSubmittedAt(now);
        submission.setIsLate(isLate);
        submission.setStatus("SUBMITTED");

        return submissionRepository.save(submission);
    }
    
    public List<Integer> getSubmittedAssignmentIds(Integer enrollmentId) {
        return repository.findSubmittedAssignmentIds(enrollmentId);
    }
    
    public Optional<SubmissionBean> getMySubmission(
            Integer assignmentId,
            Integer enrollmentId) {

        return submissionRepository.findByAssignmentAndEnrollment(
                assignmentId,
                enrollmentId
        );
    }
    
    public List<Integer> getLateAssignmentIds(Integer enrollmentId) {
        return repository.findLateAssignmentIds(enrollmentId);
    }
    
	/* for assignment download
	 * 
	 * public Optional<String> getAttachmentPath(Integer submissionId, Integer
	 * enrollmentId) { return submissionRepository.findAttachmentBySubmissionId(
	 * submissionId, enrollmentId ); }
	 */
}