package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentAssignmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.SubmissionRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Service
public class StudentAssignmentService {

    @Autowired
    private StudentAssignmentRepository repository;

    @Autowired
    private SubmissionRepository submissionRepository;

    // 🟢 REQUIRED BY GET: /student/assignments (Line 38)
    public List<AssignmentBean> getStudentAssignments(int userId) {
        return repository.findAssignmentsByStudentId(userId);
    }

    // 🟢 REQUIRED BY GET: /student/assignments (Line 39)
    public List<SubmissionBean> getSubmittedAssignments(int userId) {
        return repository.findSubmittedAssignments(userId);
    }

    // 🟢 REQUIRED BY GET: /student/assignments (Line 40)
    public List<SubmissionBean> getLateAssignments(int userId) {
        return repository.findLateAssignments(userId);
    }

    // 🟢 REQUIRED BY GET: /student/assignments/submission/{id} (Line 103)
    public Optional<SubmissionBean> getMySubmission(Integer assignmentId, int userId) {
        AssignmentBean assignment = repository.findAssignmentById(assignmentId).orElse(null);
        if (assignment == null) {
            return Optional.empty();
        }

        // Resolves enrollment ID using the verified user session parameters
        Integer enrollmentId = submissionRepository.findEnrollmentIdByStudentAndBatch(userId, assignment.getBatchId()).orElse(0);
        return submissionRepository.findByAssignmentAndEnrollment(assignmentId, enrollmentId);
    }

    // 🟢 REQUIRED BY POST: /student/assignments/submit (Line 89)
    public int submitAssignment(SubmissionBean submission, int currentUserId) {
        AssignmentBean assignment = repository.findAssignmentById(submission.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found!"));

        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.isAfter(assignment.getDueAt());

        if (isLate) {
            if (!Boolean.TRUE.equals(assignment.getAllowLateSubmit())) {
                throw new IllegalArgumentException("Assignment deadline has passed. Late submission is not allowed.");
            }
            if (assignment.getLateDeadline() == null || now.isAfter(assignment.getLateDeadline())) {
                throw new IllegalArgumentException("Late submission deadline has passed!");
            }
        }

        // Looks up the student's authentic active enrollment primary key inside MySQL to prevent foreign key errors
        Integer verifiedEnrollmentId = submissionRepository.findEnrollmentIdByStudentAndBatch(currentUserId, assignment.getBatchId())
                .orElseThrow(() -> new IllegalArgumentException("You are not actively enrolled in the batch for this assignment!"));

        submission.setEnrollmentId(verifiedEnrollmentId);

        Optional<SubmissionBean> existingSubmission = submissionRepository.findByAssignmentAndEnrollment(
                submission.getAssignmentId(), submission.getEnrollmentId());

        if (existingSubmission.isPresent()) {
            throw new IllegalArgumentException("You have already submitted this assignment!");
        }
        
        submission.setAttemptNo(1);
        submission.setSubmittedAt(now);
        submission.setIsLate(isLate);
        submission.setStatus("SUBMITTED");

        return submissionRepository.save(submission);
    }
    // 🟢 ADD THIS: Relay the total count metric to your controller
    public int getTotalAssignmentsCount(int userId) {
        return repository.getTotalAssignmentsCount(userId);
    }

    // 🟢 ADD THIS: Relay the chunked paginated array list to your controller
    public List<AssignmentBean> getPaginatedStudentAssignments(int userId, int page, int pageSize) {
        return repository.findPaginatedAssignmentsByStudentId(userId, page, pageSize);
    }

}
