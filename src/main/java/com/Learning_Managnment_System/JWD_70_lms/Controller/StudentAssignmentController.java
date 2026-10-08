// 🟢 REPLACE YOUR StudentAssignmentController.java METHODS WITH THIS UPDATED BLOCK
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
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

    private final StudentAssignmentService service;

    public StudentAssignmentController(StudentAssignmentService service) {
        this.service = service;
    }

    @GetMapping("/assignments")
    public String showAssignments(
            @RequestParam(value = "page", defaultValue = "0") int page, // 🟢 FIXED: Accept page parameter (defaults to first page)
            Model model, 
            HttpSession session) {
            
        LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
        if (currentLogin == null) {
            return "redirect:/login"; 
        }

        int activeUserId = currentLogin.getUser_id();
        int pageSize = 6; // Adjust this number to change how many cards display per page

        // 🟢 FIXED: Calculate pagination counts dynamically
        int totalItems = service.getTotalAssignmentsCount(activeUserId);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // 🟢 FIXED: Fetch only the chunk of assignments for the CURRENT page (e.g. LIMIT 6 OFFSET 0)
        List<AssignmentBean> paginatedAssignments = service.getPaginatedStudentAssignments(activeUserId, page, pageSize);

        // Map live page calculation counts straight down to the model context
        model.addAttribute("assignments", paginatedAssignments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages); // 🟢 THIS MAKES THE PAGINATION BAR APPEAR!
        
        model.addAttribute("submittedAssignments", service.getSubmittedAssignments(activeUserId));
        model.addAttribute("lateAssignments", service.getLateAssignments(activeUserId));
        
        return "student-assignment-list";
    }



    @GetMapping("/assignments/submit/{id}")
    public String showSubmitForm(@PathVariable Integer id, Model model, HttpSession session) {
        LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
        if (currentLogin == null) return "redirect:/login";

        SubmissionBean submission = new SubmissionBean();
        submission.setAssignmentId(id);
        model.addAttribute("submission", submission);
        return "student-submit-form";
    }
    
    @PostMapping("/assignments/submit")
    public String submitAssignment(
            @ModelAttribute SubmissionBean submission,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session, // 🟢 Inject session context mapping payload container
            RedirectAttributes redirectAttributes) throws IOException {

        // 🟢 FIXED: Protect authentication scope boundaries
        LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
        if (currentLogin == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/login";
        }

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
            // 🟢 FIXED: Pass the true session user_id to resolve enrollment safely inside service tier
            service.submitAssignment(submission, currentLogin.getUser_id());
            redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/assignments";
    }
    
    @GetMapping("/assignments/submission/{id}")
    public String showMySubmission(@PathVariable Integer id, Model model, HttpSession session) {
        LoginBean currentLogin = (LoginBean) session.getAttribute("currentUser");
        if (currentLogin == null) return "redirect:/login";

        Optional<SubmissionBean> submission = service.getMySubmission(id, currentLogin.getUser_id());
        if (submission.isEmpty()) {
            return "redirect:/student/assignments";
        }
        model.addAttribute("submission", submission.get());
        return "student-submission-detail";
    }
}
