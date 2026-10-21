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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    
    private final Path fileStorageLocation = Paths.get("uploads/assignments").toAbsolutePath().normalize();

    public List<AssignmentBean> getStudentAssignments(int userId) {
        return repository.findAssignmentsByStudentId(userId);
    }

    public List<SubmissionBean> getSubmittedAssignments(int userId) {
        return repository.findSubmittedAssignments(userId);
    }

    public List<SubmissionBean> getLateAssignments(int userId) {
        return repository.findLateAssignments(userId);
    }

    public Optional<SubmissionBean> getMySubmission(Integer assignmentId, int userId) {
        AssignmentBean assignment = repository.findAssignmentById(assignmentId).orElse(null);
        if (assignment == null) {
            return Optional.empty();
        }
        Integer enrollmentId = submissionRepository.findEnrollmentIdByStudentAndBatch(userId, assignment.getBatchId()).orElse(0);
        return submissionRepository.findByAssignmentAndEnrollment(assignmentId, enrollmentId);
    }

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

    public int getTotalAssignmentsCount(int userId, String title, String status) {
        return repository.getTotalAssignmentsCount(userId, title, status);
    }

    public List<AssignmentBean> getPaginatedStudentAssignments(int userId, String title, String status, int page, int pageSize) {
        return repository.findPaginatedAssignmentsByStudentId(userId, title, status, page, pageSize);
    }

    /**
     * Handles file system processing infrastructure and applies state logic via clean repository calls.
     */
    public void updateStudentSubmission(int submissionId, String answerText, MultipartFile file) throws IOException {
        String attachmentPath = null;
        
        // Process new file upload replacement if student attaches a resource document
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

        // Delegate pure database operations down safely to the separated layer
        repository.updateUngradedSubmission(submissionId, answerText, attachmentPath);
    }

    public int getUserIdByEmail(String email) {
        return repository.findUserIdByEmail(email);
    }
    
    public Resource loadTeacherFileAsResource(String rawFileName) {
        try {
            if (rawFileName == null || rawFileName.trim().isEmpty()) {
                return null;
            }

            // 🟢 SANITIZATION LAYER: Isolate the pure filename out of the complex query path string
            String cleanFileName = rawFileName;
            if (cleanFileName.contains("/")) {
                cleanFileName = cleanFileName.substring(cleanFileName.lastIndexOf("/") + 1);
            }
            if (cleanFileName.contains("\\")) {
                cleanFileName = cleanFileName.substring(cleanFileName.lastIndexOf("\\") + 1);
            }

            // Target the unified file directly within "uploads/assignments" path boundary safely
            Path targetFilePath = this.fileStorageLocation.resolve(cleanFileName).normalize();
            Resource resource = new UrlResource(targetFilePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}