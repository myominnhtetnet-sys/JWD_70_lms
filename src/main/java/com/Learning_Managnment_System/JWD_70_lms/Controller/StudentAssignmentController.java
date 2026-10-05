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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.StudentAssignmentService;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

    private final StudentAssignmentService service;

    public StudentAssignmentController(
            StudentAssignmentService service) {
        this.service = service;
    }

    @GetMapping("/assignments")
    public String showAssignments(Model model) {

        model.addAttribute( "assignments",service.getAllAssignments());
        model.addAttribute("submittedAssignmentIds", service.getSubmittedAssignmentIds(1));
        model.addAttribute( "lateAssignmentIds",service.getLateAssignmentIds(1));
        return "student-assignment-list";
    }
   
    @GetMapping("/assignments/submit/{id}")
    public String showSubmitForm( @PathVariable Integer id,Model model) {
        SubmissionBean submission = new SubmissionBean();
        submission.setAssignmentId(id);
        model.addAttribute("submission", submission);
        return "student-submit-form";
    }
    
    @PostMapping("/assignments/submit")
    public String submitAssignment(
            @ModelAttribute SubmissionBean submission,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) throws IOException {

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")).toLowerCase();
            }

            if (!List.of(".pdf", ".docx", ".zip", ".txt").contains(extension)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Only PDF, DOCX, ZIP and TXT files are allowed.");
                return "redirect:/student/assignments";
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "File size must be 5MB or less.");
                return "redirect:/student/assignments";
            }

            Path uploadPath = Paths.get("uploads");
            Files.createDirectories(uploadPath);
            String savedFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath);
            submission.setAttachment("uploads/" + savedFileName);
        }
        submission.setEnrollmentId(1);

        try {
            service.submitAssignment(submission);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Assignment submitted successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage() );
        }
        return "redirect:/student/assignments";
        
    }
    
    @GetMapping("/assignments/submission/{id}")
    public String showMySubmission( @PathVariable Integer id, Model model) {

        Optional<SubmissionBean> submission = service.getMySubmission(id, 1);
        if (submission.isEmpty()) {
            return "redirect:/student/assignments";
        }
        model.addAttribute("submission", submission.get());
        return "student-submission-detail";
    }
}