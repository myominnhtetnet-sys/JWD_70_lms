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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            RedirectAttributes redirectAttributes) {

        try {
            int reviewedByAdminId = 1; 

            // Process document upload if a file was selected in the modal
            if (documentFile != null && !documentFile.isEmpty()) {
                String originalFilename = documentFile.getOriginalFilename();
                String uploadDir = "uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String uniqueFilename = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
                Path destinationPath = Paths.get(uploadDir + uniqueFilename);
                Files.copy(documentFile.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                application.setSupportingDocument(uniqueFilename);
                
                // Update database supporting document column if method exists
                // applicationsRepository.updateDocument(application.getApplicationId(), uniqueFilename);
            }

            applicationsRepository.reviewApplication(
                application.getApplicationId(),
                application.getStatus(),
                application.getApprovedAmount(),
                reviewedByAdminId,
                application.getReviewNotes()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship application review saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save application review: " + e.getMessage());
        }

        return "redirect:/admin/scholarship-applications-review";
    }

    /**
     * Endpoint for the row-level inline upload button directly inside the table.
     */
    @PostMapping("/upload-inline")
    public String uploadInlineDocument(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("documentFile") MultipartFile documentFile,
            RedirectAttributes redirectAttributes) {
        try {
            if (documentFile != null && !documentFile.isEmpty()) {
                String originalFilename = documentFile.getOriginalFilename();
                String uploadDir = "uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String uniqueFilename = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
                Path destinationPath = Paths.get(uploadDir + uniqueFilename);
                Files.copy(documentFile.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                applicationsRepository.updateDocument(applicationId, uniqueFilename);
                
                redirectAttributes.addFlashAttribute("successMessage", "Document uploaded successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload document: " + e.getMessage());
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

            Path filePath = Paths.get(docPath).normalize();

            if (!Files.exists(filePath)) {
                filePath = Paths.get("uploads").resolve(docPath).normalize();
            }

            if (!Files.exists(filePath)) {
                filePath = Paths.get("src/main/resources/static").resolve(docPath).normalize();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("Document file not found at path: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

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