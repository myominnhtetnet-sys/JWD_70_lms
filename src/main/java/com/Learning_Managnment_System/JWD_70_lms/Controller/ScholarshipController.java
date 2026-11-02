package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student/scholarship")
public class ScholarshipController {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String showScholarshipPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> student = studentRepository.findByEmail(email);
        int userId = 0;

        if (student != null) {
            Object idObj = student.get("user_id"); 
            if (idObj instanceof Number) {
                userId = ((Number) idObj).intValue();
            }
            model.addAttribute("fullName", student.get("full_name"));
        }

        // 1. Fetch student's submitted histories
        List<ScholarshipApplicationBean> scholarships = scholarshipRepository.getApplicationsByUserId(userId);
        model.addAttribute("scholarships", scholarships);

        // 2. 🟢 FIXED: Pass the resolved userId into the repository to hide already-applied programs
        List<Map<String, Object>> availablePrograms = scholarshipRepository.findAvailablePrograms(userId);
        model.addAttribute("programs", availablePrograms);

        return "scholarship"; 
    }
 
    @GetMapping("/details/{id}")
    public String viewScholarshipDetail(@PathVariable("id") int applicationId, HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> detail = scholarshipRepository.getApplicationDetailById(applicationId);
        model.addAttribute("detail", detail);
        model.addAttribute("userEmail", email);

        return "scholarship-detail"; 
    }

    @PostMapping("/apply")
    public String processApplication(
            @RequestParam("scholarshipId") int scholarshipId,
            @RequestParam("reason") String reason,
            @RequestParam("scholarshipFile") org.springframework.web.multipart.MultipartFile scholarshipFile, // 🟢 FIXED: Catches the real file stream packet from your form input
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        Map<String, Object> student = studentRepository.findByEmail(email);
        if (student == null || student.get("user_id") == null) {
            redirectAttributes.addFlashAttribute("error", "Student profile mapping missing.");
            return "redirect:/student/scholarship";
        }

        int userId = ((Number) student.get("user_id")).intValue();

        if (scholarshipRepository.hasAlreadyApplied(userId, scholarshipId)) {
            redirectAttributes.addFlashAttribute("error", "You have already submitted an active application for this program.");
            return "redirect:/student/scholarship";
        }

        // ==========================================
        // 🟢 REAL-TIME FILE STREAM DISPATCH HOOK
        // ==========================================
        String savedFileName = "document.pdf"; 
        
        if (scholarshipFile != null && !scholarshipFile.isEmpty()) {
            try {
                String originalName = scholarshipFile.getOriginalFilename();
                // Strip spaces and append a unique millisecond timestamp prefix metric
                savedFileName = System.currentTimeMillis() + "_" + (originalName != null ? originalName.replaceAll("\\s+", "_") : "scholarship.pdf");
                
                // Route to the external directory folder managed by WebConfig.java
                String uploadDirectoryPath = System.getProperty("user.dir") + "/uploads/scholarships/";
                java.io.File directory = new java.io.File(uploadDirectoryPath);
                if (!directory.exists()) {
                    directory.mkdirs(); // Build external directory tree natively if missing
                }
                
                // Write binary packet data to disk
                java.io.File destinationFile = new java.io.File(directory.getAbsolutePath() + java.io.File.separator + savedFileName);
                scholarshipFile.transferTo(destinationFile);
                System.out.println("💾 FILE TRANSACTION LOG -> Saved to: " + destinationFile.getAbsolutePath());
                
            } catch (Exception e) {
                System.err.println("❌ CRITICAL UPLOAD EXCEPTION -> " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Failed to transfer file content payload stream.");
                return "redirect:/student/scholarship";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Please provide a valid supporting document PDF attachment.");
            return "redirect:/student/scholarship";
        }

        // Save the dynamically generated unique fileName string into your database query loop row
        int rows = scholarshipRepository.insertApplication(userId, scholarshipId, reason.trim(), savedFileName);

        if (rows > 0) {
            redirectAttributes.addFlashAttribute("success", "Your scholarship application and document have been logged successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Database validation failed to commit application.");
        }

        return "redirect:/student/scholarship";
    }

}
