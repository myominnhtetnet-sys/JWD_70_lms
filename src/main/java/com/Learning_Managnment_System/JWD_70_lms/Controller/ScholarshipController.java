package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRespority;
// Import your discount type repository or service here (adjust package name if needed)
// import com.Learning_Managnment_System.JWD_70_lms.Repository.DiscountTypeRepository;

import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipApplicationBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/scholarships")
public class ScholarshipController {

    @Autowired
    private ScholarshipRespority scholarshipRepository;

    // TODO: Autowire your discount type repository to populate dropdowns
    // @Autowired
    // private DiscountTypeRepository discountTypeRepository;

    // =========================================
    // SCHOLARSHIP LIST
    // =========================================
    @GetMapping({"", "/", "/list"})
    public String listScholarships(Model model) {
        List<ScholarshipBean> scholarships = scholarshipRepository.getAllScholarships();
        model.addAttribute("scholarships", scholarships);
        return "Scholarship-list";
    }

    // =========================================
    // ADD SCHOLARSHIP FORM
    // =========================================
    @GetMapping("/new")
    public String showAddForm(Model model) {
        ScholarshipBean scholarship = new ScholarshipBean();
        scholarship.setIsActive(true); // Default active status
        model.addAttribute("scholarship", scholarship);
        
        // Load discount types for the dropdown
        // model.addAttribute("discountTypes", discountTypeRepository.getAllDiscountTypes());
        
        return "Scholarship-form";
    }

    // =========================================
    // SAVE / UPDATE SCHOLARSHIP
    // =========================================
    @PostMapping("/save")
    public String saveScholarship(
            @ModelAttribute("scholarship") ScholarshipBean scholarship,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            boolean isNew = scholarship.getScholarshipId() == null || scholarship.getScholarshipId() <= 0;

            if (isNew) {
                scholarshipRepository.createScholarship(scholarship);
                redirectAttributes.addFlashAttribute("successMessage", "Scholarship created successfully.");
            } else {
                scholarshipRepository.updateScholarship(scholarship.getScholarshipId(), scholarship);
                redirectAttributes.addFlashAttribute("successMessage", "Scholarship updated successfully.");
            }

            return "redirect:/admin/scholarships/list";

        } catch (DataIntegrityViolationException e) {
            // Catches both duplicate keys AND foreign key constraint failures
            if (e.getMessage() != null && e.getMessage().contains("fk_scholarships_type")) {
                model.addAttribute("errorMessage", "Selected discount type does not exist. Please choose a valid type.");
            } else {
                model.addAttribute("errorMessage", "A database error occurred. The scholarship name or details may already exist.");
            }
            
            // CRITICAL: Re-populate dropdown options so the form renders correctly on error return
            // model.addAttribute("discountTypes", discountTypeRepository.getAllDiscountTypes());
            
            return "Scholarship-form";
        }
    }

    // =========================================
    // EDIT SCHOLARSHIP FORM
    // =========================================
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model) {

        ScholarshipBean scholarship = scholarshipRepository.getScholarshipById(id);

        if (scholarship == null) {
            return "redirect:/admin/scholarships/list";
        }

        model.addAttribute("scholarship", scholarship);
        
        // Load discount types for the dropdown
        // model.addAttribute("discountTypes", discountTypeRepository.getAllDiscountTypes());
        
        return "Scholarship-form";
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
        if (student != null) {
          
            Object idObj = student.get("user_id"); 
            int userId = 0;
            if (idObj instanceof Number) {
                userId = ((Number) idObj).intValue();
            }

          
            List<ScholarshipApplicationBean> scholarships = scholarshipRepository.getApplicationsByUserId(userId);
            
            model.addAttribute("scholarships", scholarships);
            model.addAttribute("fullName", student.get("full_name"));
        }

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

    // =========================================
    // DELETE SCHOLARSHIP
    // =========================================
    @GetMapping("/delete/{id}")
    public String deleteScholarship(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            scholarshipRepository.deleteScholarship(id);
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship deleted successfully.");
        } catch (DataIntegrityViolationException e) {
            // Catches foreign key violations (e.g., student applications tied to this scholarship)[cite: 2]
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete this scholarship because student applications are already linked to it.");
        }

        return "redirect:/admin/scholarships/list";
    }
    
}