
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;

@Controller
@RequestMapping("/admin/scholarships")
public class ScholarshipController {

    private final ScholarshipRespority scholarshipRepository;

    public ScholarshipController(
            ScholarshipRespority scholarshipRepository) {
        this.scholarshipRepository = scholarshipRepository;
    }

    // Scholarship list
    @GetMapping({"", "/", "/list"})
    public String listScholarships(Model model) {
        List<ScholarshipBean> scholarships =
                scholarshipRepository.getAllScholarships();

        model.addAttribute("scholarships", scholarships);

        return "Scholarship-list";
    }

    // Show create form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        ScholarshipBean scholarship = new ScholarshipBean();
        scholarship.setIsActive(true);

        model.addAttribute("scholarship", scholarship);

        return "Scholarship-form";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        ScholarshipBean scholarship =
                scholarshipRepository.getScholarshipById(id);

        if (scholarship == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Scholarship not found.");

            return "redirect:/admin/scholarships/list";
        }

        model.addAttribute("scholarship", scholarship);

        return "Scholarship-form";
    }

    // Save new scholarship or update existing scholarship
    @PostMapping("/save")
    public String saveScholarship(
            @ModelAttribute("scholarship") ScholarshipBean scholarship,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isNew = scholarship.getScholarshipId() == null
                || scholarship.getScholarshipId() <= 0;

        try {
            if (isNew) {
                int generatedId =
                        scholarshipRepository.createScholarship(scholarship);

                if (generatedId <= 0) {
                    model.addAttribute(
                            "errorMessage",
                            "Unable to create scholarship.");

                    return "Scholarship-form";
                }

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Scholarship created successfully.");

            } else {
                int updatedRows =
                        scholarshipRepository.updateScholarship(
                                scholarship.getScholarshipId(),
                                scholarship);

                if (updatedRows == 0) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Scholarship not found or no changes were made.");
                } else {
                    redirectAttributes.addFlashAttribute(
                            "successMessage",
                            "Scholarship updated successfully.");
                }
            }

            return "redirect:/admin/scholarships/list";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(
                    "errorMessage",
                    "Database constraint error. Please check the course, "
                    + "discount type, and other required values.");

            return "Scholarship-form";
        }
    }

    // Delete scholarship
    @GetMapping("/delete/{id}")
    public String deleteScholarship(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            int deletedRows =
                    scholarshipRepository.deleteScholarship(id);

            if (deletedRows > 0) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Scholarship deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Scholarship not found.");
            }

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete this scholarship because "
                    + "student applications may be linked to it.");
        }

        return "redirect:/admin/scholarships/list";
    }
}
