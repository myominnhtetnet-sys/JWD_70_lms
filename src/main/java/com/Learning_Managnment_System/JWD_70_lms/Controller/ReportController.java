package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

 
    @GetMapping("/admin/report")
    public String viewReportsPage(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "report";
    }

    // Handle report form submission and add to the in-memory list
    @PostMapping("/admin/report/generate")
    public String handleReportGeneration(
            @RequestParam("reportTitle") String reportTitle,
            @RequestParam("category") String category,
            @RequestParam("format") String format,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            reportService.processReportUpload(reportTitle, category, format, file);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Report generated and stored in memory successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to process document: " + e.getMessage());
        }

        return "redirect:/admin/report";
    }
}