package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.TeacherExamResultService;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamGradingForm;

@Controller
@RequestMapping("/teacher/exam-results")
public class TeacherExamResultController {

    private final TeacherExamResultService service;

    // Local testing only.
    // Replace 1L with the created_by ID of the exam you want to test.
    private static final Long TEST_TEACHER_ID = 1L;

    public TeacherExamResultController(
            TeacherExamResultService service) {
        this.service = service;
    }

    @GetMapping
    public String listResults(
            @RequestParam(name = "attemptId", required = false)
            Long attemptId,
            @RequestParam(name = "mode", defaultValue = "view")
            String mode,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long teacherId = TEST_TEACHER_ID;

        loadList(model, teacherId);

        if (attemptId != null) {
            try {

                Map<String, Object> attempt =
                        service.getAttempt(attemptId, teacherId);

                List<Map<String, Object>> answers =
                        service.getAnswers(attempt);

                model.addAttribute("attempt", attempt);
                model.addAttribute("examAnswers", answers);
                model.addAttribute(
                        "gradingForm",
                        service.createForm(attempt, answers));

                model.addAttribute(
                        "mode",
                        "grade".equals(mode) ? "grade" : "view");

            } catch (IllegalArgumentException e) {

                redirectAttributes.addFlashAttribute(
                        "error", e.getMessage());

                return "redirect:/teacher/exam-results";
            }
        }

        return "teacher-exam-result-list";
    }

    @PostMapping("/{attemptId}/grade")
    public String saveGrades(
            @PathVariable("attemptId") Long attemptId,
            @ModelAttribute("gradingForm") ExamGradingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long teacherId = TEST_TEACHER_ID;

        Map<String, Object> attempt;

        try {

            attempt = service.getAttempt(attemptId, teacherId);

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/teacher/exam-results";
        }

        List<Map<String, Object>> answers =
                service.getAnswers(attempt);

        try {

            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException(
                        "Please enter valid numbers for the marks.");
            }

            service.gradeAttempt(attemptId, teacherId, form);

            redirectAttributes.addFlashAttribute(
                    "success", "Exam graded successfully!");

            return "redirect:/teacher/exam-results";

        } catch (IllegalArgumentException e) {

            // Preserve entered marks when validation fails.
            loadList(model, teacherId);

            model.addAttribute("attempt", attempt);
            model.addAttribute("examAnswers", answers);
            model.addAttribute("mode", "grade");
            model.addAttribute("error", e.getMessage());

            return "teacher-exam-result-list";
        }
    }

    private void loadList(Model model, Long teacherId) {

        List<Map<String, Object>> results =
                service.getResults(teacherId);

        long graded = results.stream()
                .filter(row -> "GRADED".equals(row.get("status")))
                .count();

        model.addAttribute("results", results);
        model.addAttribute("totalCount", results.size());
        model.addAttribute("gradedCount", graded);
        model.addAttribute("pendingCount", results.size() - graded);
    }
}

