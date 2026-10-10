package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate; // 🟢 Added import for direct safe database updates
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 🟢 Added import for file stream handling

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

    @Autowired
    private JdbcTemplate jdbcTemplate; // 🟢 Injected directly to execute non-static update statements safely

    // REQUIRED BY GET: /student/assignments
    public List<AssignmentBean> getStudentAssignments(int userId) {
        return repository.findAssignmentsByStudentId(userId);
    }

    // REQUIRED BY GET: /student/assignments
    public List<SubmissionBean> getSubmittedAssignments(int userId) {
        return repository.findSubmittedAssignments(userId);
    }

    // REQUIRED BY GET: /student/assignments
    public List<SubmissionBean> getLateAssignments(int userId) {
        return repository.findLateAssignments(userId);
    }

    // REQUIRED BY GET: /student/assignments/submission/{id}
    public Optional<SubmissionBean> getMySubmission(Integer assignmentId, int userId) {
        AssignmentBean assignment = repository.findAssignmentById(assignmentId).orElse(null);
        if (assignment == null) {
            return Optional.empty();
        }

        // Resolves enrollment ID using the verified user session parameters
        Integer enrollmentId = submissionRepository.findEnrollmentIdByStudentAndBatch(userId, assignment.getBatchId()).orElse(0);
        return submissionRepository.findByAssignmentAndEnrollment(assignmentId, enrollmentId);
    }

    // REQUIRED BY POST: /student/assignments/submit
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

    // 🟢 UPDATED: Relays the total count metric passing search parameters down to repository
    public int getTotalAssignmentsCount(int userId, String title, String status) {
        return repository.getTotalAssignmentsCount(userId, title, status);
    }

    // 🟢 UPDATED: Relays the chunked paginated array list passing search parameters down to repository
    public List<AssignmentBean> getPaginatedStudentAssignments(int userId, String title, String status, int page, int pageSize) {
        return repository.findPaginatedAssignmentsByStudentId(userId, title, status, page, pageSize);
    }

    // ==========================================================================
    // 🟢 NEW ADDITION: PROCESSES FILE OVERRIDES AND SAVES MODIFIED SUBMISSIONS
    // ==========================================================================
    public void updateStudentSubmission(int submissionId, String answerText, MultipartFile file) throws IOException {
        String attachmentPath = null;
        
        // 1. Process new file upload replacement if student attaches a resource document
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get("uploads");
            Files.createDirectories(uploadPath);
            
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }
            
            String savedFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath);
            
            attachmentPath = "uploads/" + savedFileName;
        }

        // 2. Execute SQL query on live connection block ensuring ungraded entries are isolated
        if (attachmentPath != null) {
            String sql = "UPDATE submissions SET answer_text = ?, attachment = ?, submitted_at = NOW() WHERE submission_id = ? AND status != 'GRADED'";
            jdbcTemplate.update(sql, answerText, attachmentPath, submissionId);
        } else {
            String sql = "UPDATE submissions SET answer_text = ?, submitted_at = NOW() WHERE submission_id = ? AND status != 'GRADED'";
            jdbcTemplate.update(sql, answerText, submissionId);
        }
    }
}
