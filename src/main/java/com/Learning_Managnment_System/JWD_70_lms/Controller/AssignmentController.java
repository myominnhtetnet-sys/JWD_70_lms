package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.BatchRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.LessonRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.AssignmentService;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonBean;

@Controller
@RequestMapping("/teacher")
public class AssignmentController {

	private final AssignmentService assignmentService;
    private final BatchRepository batchRepository;
    private final LessonRepository lessonRepository;

    public AssignmentController(
            AssignmentService assignmentService,
            BatchRepository batchRepository,
            LessonRepository lessonRepository) {

        this.assignmentService = assignmentService;
        this.batchRepository = batchRepository;
        this.lessonRepository = lessonRepository;
    }

    // 1. SHOW ALL ASSIGNMENTS
    @GetMapping("/assignments")
    public String listAssignments(Model model) {

        List<AssignmentBean> assignments =
                assignmentService.getAllAssignments();

        model.addAttribute("assignments", assignments);

        return "assignment-list";
    }
    
 // SEARCH & FILTER ASSIGNMENTS

    @GetMapping("/assignments/search")
    public String searchAssignments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) String status,
            Model model) {

        List<AssignmentBean> assignments =
                assignmentService.searchAssignments(
                        title,
                        batchId,
                        status
                );

        model.addAttribute("assignments", assignments);
        model.addAttribute("title", title);
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("selectedStatus", status);
        loadDropdownData(model);
        return "assignment-list";
    }

    // 2. SHOW CREATE FORM
    @GetMapping("/assignments/create")
    public String showCreateForm(Model model) {

        model.addAttribute("assignment", new AssignmentBean());
        loadDropdownData(model);
        return "assignment-form";
    }

    // 3. SAVE NEW ASSIGNMENT
    @PostMapping("/assignments/save")
    public String saveAssignment(
            @ModelAttribute("assignment") AssignmentBean assignment,
            RedirectAttributes redirectAttributes) {

        try {

            int newId = assignmentService.createAssignment(assignment);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Assignment created successfully! ID: " + newId
            );

            return "redirect:/teacher/assignments";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/teacher/assignments/create";
        }
    }

    // 4. SHOW EDIT FORM
    @GetMapping("/assignments/edit/{id}")
    public String showEditForm(@PathVariable Integer id,Model model) {

        AssignmentBean assignment =assignmentService.getAssignmentById(id);
        model.addAttribute("assignment", assignment);
        loadDropdownData(model);
        return "assignment-form";
    }

    // 5. UPDATE ASSIGNMENT
    @PostMapping("/assignments/update")
    public String updateAssignment(
            @ModelAttribute("assignment") AssignmentBean assignment,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            assignmentService.updateAssignment(assignment);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Assignment updated successfully!"
            );

            return "redirect:/teacher/assignments";

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("assignment", assignment);
            return "assignment-form";
        }
    }

    // 6. DELETE ASSIGNMENT
    @PostMapping("/assignments/delete/{id}")
    public String deleteAssignment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            assignmentService.deleteAssignment(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Assignment deleted successfully!"
            );

        } catch (IllegalArgumentException e) {	
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/teacher/assignments";
    }
    
    private void loadDropdownData(Model m) {

        List<BatchBean> batches = batchRepository.findAll();
        List<LessonBean> lessons = lessonRepository.findAll();

        m.addAttribute("batches", batches);
        m.addAttribute("lessons", lessons);
    }
    
}