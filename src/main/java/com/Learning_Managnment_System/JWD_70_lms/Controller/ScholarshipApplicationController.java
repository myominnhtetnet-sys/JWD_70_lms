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

    // =========================================
    // SCHOLARSHIP APPLICATIONS REVIEW QUEUE
    // =========================================
    @GetMapping({"", "/", "/list"})
    public String listApplications(Model model) {
        List<ScholarshipApplicationsBean> applications = applicationsRepository.getAllApplications();
        model.addAttribute("applications", applications);
        return "scholarship-applications-review";
    }

    // =========================================
    // REVIEW / UPDATE SCHOLARSHIP APPLICATION
    // =========================================
    @PostMapping("/review")
    public String reviewApplication(
            @ModelAttribute("application") ScholarshipApplicationsBean application,
            RedirectAttributes redirectAttributes) {

        try {
            // NOTE: Change '1' to your logged-in admin's user ID dynamically if you have Spring Security session handling
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
}