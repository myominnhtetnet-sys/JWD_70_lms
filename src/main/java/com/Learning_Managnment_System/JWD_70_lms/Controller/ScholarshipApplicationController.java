package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipApplicationsRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Serves and streams the actual uploaded supporting document file (PDF, Image, etc.) 
     * directly to the browser for viewing or downloading.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("id") Long id) {
        try {
            ScholarshipApplicationsBean app = applicationsRepository.findById(id);
            
            if (app == null || app.getSupportingDocument() == null || app.getSupportingDocument().trim().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String docPath = app.getSupportingDocument().trim();

            // 1. Check direct path relative to project root (e.g. ./docs/transcript4.pdf)
            Path filePath = Paths.get(docPath).normalize();

            // 2. Fallback: check inside 'uploads' directory
            if (!Files.exists(filePath)) {
                filePath = Paths.get("uploads").resolve(docPath).normalize();
            }

            // 3. Fallback: check inside static assets directory
            if (!Files.exists(filePath)) {
                filePath = Paths.get("src/main/resources/static").resolve(docPath).normalize();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("Document file not found at path: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // Determine content type dynamically
            String contentType = "application/octet-stream";
            String filenameLower = docPath.toLowerCase();
            if (filenameLower.endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            } else if (filenameLower.endsWith(".jpg") || filenameLower.endsWith(".jpeg")) {
                contentType = MediaType.IMAGE_JPEG_VALUE;
            } else if (filenameLower.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}