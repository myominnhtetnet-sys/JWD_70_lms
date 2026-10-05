
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.TeacherGradingService;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Controller
@RequestMapping("/teacher")
public class TeacherGradingController {

    private final TeacherGradingService service;

    public TeacherGradingController(TeacherGradingService service) {
        this.service = service;
    }

    // Show all submissions
    @GetMapping("/submissions")
    public String showSubmissions(Model model) {

        model.addAttribute("submissions",
                service.getAllSubmissions());

        return "teacher-submission-list";
    }

    // Open grading form
    @GetMapping("/submissions/grade/{id}")
    public String showGradingForm(
            @PathVariable Integer id,
            Model model) {

        SubmissionBean submission = service.getSubmissionById(id);

        model.addAttribute("submission", submission);

        return "teacher-grading-form";
    }

    // Save grading
    @PostMapping("/submissions/grade")
    public String gradeSubmission(
            @RequestParam Integer submissionId,
            @RequestParam BigDecimal score,
            @RequestParam String feedback,
            RedirectAttributes redirectAttributes) {

        try {

            service.gradeSubmission(
                    submissionId,
                    score,
                    feedback
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Student submission graded successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/teacher/submissions/grade/" + submissionId;
        }

        return "redirect:/teacher/submissions";
    }
}