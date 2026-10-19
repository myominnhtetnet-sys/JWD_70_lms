
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Learning_Managnment_System.JWD_70_lms.Service.FinalGradeService;
import com.Learning_Managnment_System.JWD_70_lms.model.FinalGradeBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/teacher/final-grades")
public class FinalGradeController {

    private final FinalGradeService finalGradeService;

    public FinalGradeController(FinalGradeService finalGradeService) {
        this.finalGradeService = finalGradeService;
    }

    @GetMapping
    public String showFinalGrades(
            @RequestParam(required = false) Integer batchId,
            Model model) {

        model.addAttribute("batches", finalGradeService.getBatches());
        model.addAttribute("selectedBatchId", batchId);

        if (batchId != null) {
            List<FinalGradeBean> grades =
                    finalGradeService.calculateGrades(batchId);

            model.addAttribute("grades", grades);
            model.addAttribute("hasGrades", !grades.isEmpty());
        }

        return "final-grades";
    }

    @PostMapping("/publish")
    public String publishGrades(
            @RequestParam Integer batchId,
            HttpSession session,
            Model model) {

        Object userId = session.getAttribute("user_id");

        if (userId == null) {
            model.addAttribute("errorMessage",
                    "Login session မရှိပါ။ Login ပြန်ဝင်ပါ။");

            model.addAttribute(
                    "batches", finalGradeService.getBatches());

            return "final-grades";
        }

        try {
            int teacherId = Integer.parseInt(userId.toString());

            finalGradeService.publishGrades(batchId, teacherId);

            return "redirect:/teacher/final-grades?batchId="
                    + batchId + "&published=true";

        } catch (IllegalStateException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute(
                    "batches", finalGradeService.getBatches());

            model.addAttribute("selectedBatchId", batchId);
            model.addAttribute(
                    "grades", finalGradeService.calculateGrades(batchId));

            return "final-grades";
        }
    }
}
