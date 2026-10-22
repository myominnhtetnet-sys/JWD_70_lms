package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.TeacherGradingService;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Controller
@RequestMapping("/teacher/submissions")
public class TeacherGradingController {

    private final TeacherGradingService gradingService;

    public TeacherGradingController( TeacherGradingService gradingService) {
        this.gradingService = gradingService;
    }

    @GetMapping
    public String listSubmissions(Model model) {
        model.addAttribute("submissions",gradingService.getAllSubmissions());
        return "teacher-submission-list";
    }

    @GetMapping("/grade/{id}")
    public String showGradePage( @PathVariable Integer id,Model model,
            RedirectAttributes redirectAttributes) {

        try {
            SubmissionBean submission = gradingService.getSubmissionById(id);
            model.addAttribute("submission", submission);
            return "teacher-grading-form";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/submissions";
        }
    }

    @PostMapping("/grade")
    public String saveGrade(
            @RequestParam Integer submissionId,
            @RequestParam BigDecimal score,
            @RequestParam String feedback,
            RedirectAttributes redirectAttributes) {

        try {
            gradingService.gradeSubmission( submissionId,score,feedback);
            redirectAttributes.addFlashAttribute("success","Submission graded successfully!" );
            return "redirect:/teacher/submissions";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/submissions/grade/" + submissionId;
        }
    }
}