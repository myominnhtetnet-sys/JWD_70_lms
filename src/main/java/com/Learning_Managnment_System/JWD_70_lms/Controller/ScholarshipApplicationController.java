package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipApplicationsRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/scholarship-applications-review")
public class ScholarshipApplicationController {

    @Autowired
    private ScholarshipApplicationsRespority applicationsRepository;

    @GetMapping({"", "/", "/list"})
    public String listApplications(Model model) {
        List<ScholarshipApplicationsBean> applications = applicationsRepository.getAllApplications();
        model.addAttribute("applications", applications);
        return "scholarship-applications-review";
    }

    @PostMapping("/review")
    public String reviewApplication(
            @ModelAttribute("application") ScholarshipApplicationsBean application,
            RedirectAttributes redirectAttributes) {

        try {
            int reviewedByAdminId = 1; 

            applicationsRepository.reviewApplication(
                application.getApplicationId(),
                application.getStatus(),
                application.getApprovedAmount(),
                reviewedByAdminId,
                application.getReviewNotes()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship application review saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save application review: " + e.getMessage());
        }

        return "redirect:/admin/scholarship-applications-review";
    }

    // Loads the ornate certificate review page (view-document.html)
    @GetMapping("/download/{id}")
    public String downloadDocument(@PathVariable("id") Long id, Model model) {
        ScholarshipApplicationsBean app = applicationsRepository.findById(id);
        
        if (app == null) {
            return "redirect:/admin/scholarship-applications-review";
        }

        model.addAttribute("application", app);
        return "view-document";
    }
}