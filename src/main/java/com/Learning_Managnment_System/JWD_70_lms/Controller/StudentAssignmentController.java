package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.StudentAssignmentService;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

    private final StudentAssignmentService studentAssignmentService;

    public StudentAssignmentController(StudentAssignmentService service) {
        this.studentAssignmentService = service;
    }

    /**
     * 🟢 1. SHOW PAGINATED & FILTERED STUDENT ASSIGNMENTS LIST
     * ⚡ ALIGNED KEY FIX: Checks for 'email' to match your MyCourseController authentication state.
     */
    @GetMapping("/assignments")
    public String showAssignments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "title", required = false) String title,   
            @RequestParam(value = "status", required = false) String status, 
            Model model, 
            HttpSession session) {
            
        // 🟢 FIXED: Changed session key lookup from "currentUser" to plain "email" string
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) {
            return "redirect:/login"; 
        }

        // Fetch user ID dynamically via service using the active email token string
        int activeUserId = studentAssignmentService.getUserIdByEmail(studentEmail);
        int pageSize = 6; 

        // Compute items dynamically through service queries
        int totalItems = studentAssignmentService.getTotalAssignmentsCount(activeUserId, title, status);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        List<AssignmentBean> paginatedAssignments = studentAssignmentService.getPaginatedStudentAssignments(activeUserId, title, status, page, pageSize);

        model.addAttribute("assignments", paginatedAssignments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        model.addAttribute("submittedAssignments", studentAssignmentService.getSubmittedAssignments(activeUserId));
        model.addAttribute("lateAssignments", studentAssignmentService.getLateAssignments(activeUserId));
        
        return "student-assignment-list";
    }

    @GetMapping("/assignments/submit/{id}")
    public String showSubmitForm(@PathVariable Integer id, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        SubmissionBean submission = new SubmissionBean();
        submission.setAssignmentId(id);
        model.addAttribute("submission", submission);
        return "student-submit-form";
    }
    
    @PostMapping("/assignments/submit")
    public String submitAssignment(
            @ModelAttribute SubmissionBean submission,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session, 
            RedirectAttributes redirectAttributes) throws IOException {

        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/login";
        }

        int activeUserId = studentAssignmentService.getUserIdByEmail(studentEmail);

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }

            if (!List.of(".pdf", ".docx", ".zip", ".txt").contains(extension)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only PDF, DOCX, ZIP and TXT files are allowed.");
                return "redirect:/student/assignments";
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("errorMessage", "File size must be 5MB or less.");
                return "redirect:/student/assignments";
            }

            Path uploadPath = Paths.get("uploads");
            Files.createDirectories(uploadPath);
            String savedFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath);
            submission.setAttachment("uploads/" + savedFileName);
        }

        try {
            studentAssignmentService.submitAssignment(submission, activeUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/assignments";
    }
    
    @GetMapping("/assignments/submission/{id}")
    public String showMySubmission(@PathVariable Integer id, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        int activeUserId = studentAssignmentService.getUserIdByEmail(studentEmail);
        Optional<SubmissionBean> submission = studentAssignmentService.getMySubmission(id, activeUserId);
        if (submission.isEmpty()) {
            return "redirect:/student/assignments";
        }
        model.addAttribute("submission", submission.get());
        return "student-submission-detail";
    }
    
    @PostMapping("/assignments/submission/update")
    public String updateSubmission(
            @RequestParam("submissionId") int submissionId,
            @RequestParam("assignmentId") int assignmentId,
            @RequestParam(value = "answerText", required = false) String answerText,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) {
            return "redirect:/login";
        }

        try {
            studentAssignmentService.updateStudentSubmission(submissionId, answerText, file);
            redirectAttributes.addFlashAttribute("successMessage", "Your assignment submission has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save modifications: " + e.getMessage());
        }

        return "redirect:/student/assignments/submission/" + assignmentId;
    }
}
