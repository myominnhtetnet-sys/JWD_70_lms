package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
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
    private static final String UPLOAD_DIR = "uploads/assignments";

    public AssignmentController(
            AssignmentService assignmentService,
            BatchRepository batchRepository,
            LessonRepository lessonRepository) {

        this.assignmentService = assignmentService;
        this.batchRepository = batchRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/assignments")
    public String listAssignments(@RequestParam(defaultValue = "1") int page, Model model) {

        int size = 5;
        int totalItems = assignmentService.getTotalAssignments();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        if (totalPages == 0) {
            totalPages = 1;
        }
        
        if (page < 1) {
            page = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        List<AssignmentBean> assignments = assignmentService.getAssignmentsByPage(page, size);

        model.addAttribute("assignments", assignments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        loadDropdownData(model);

        return "assignment-list";
    }
    
    @GetMapping("/assignments/search")
    public String searchAssignments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) String status,
            Model model) {

        List<AssignmentBean> assignments =
        		assignmentService.searchAssignments( title,batchId,status);

        model.addAttribute("assignments", assignments);
        model.addAttribute("title", title);
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", assignments.size());
        loadDropdownData(model);

        return "assignment-list";
    }

    @GetMapping("/assignments/create")
    public String showCreateForm(Model model) {
        model.addAttribute( "assignment",new AssignmentBean());
        loadDropdownData(model);
        return "assignment-form";
    }

    @PostMapping("/assignments/save")
    public String saveAssignment(
            @ModelAttribute("assignment") AssignmentBean assignment,
            @RequestParam(value = "attachmentFile", required = false)
            MultipartFile attachmentFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                String fileUrl = saveAttachmentFile(attachmentFile);
                assignment.setAttachment(fileUrl);
            }

            int newId =assignmentService.createAssignment(assignment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Assignment created successfully! ID: " + newId );
            
            return "redirect:/teacher/assignments";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage",e.getMessage());
            loadDropdownData(model);
            return "assignment-form";

        } catch (IOException e) {
            model.addAttribute("errorMessage",
                    "File upload failed: " + e.getMessage());

            loadDropdownData(model);
            return "assignment-form";
        }
    }

    @GetMapping("/assignments/{id}")
    public String viewAssignment( @PathVariable Integer id, Model model) {

        AssignmentBean assignment = assignmentService.getAssignmentById(id);

        BatchBean batch =
        		batchRepository.findById(
                        assignment.getBatchId()
                ).orElseThrow(
                        () -> new IllegalArgumentException("Batch not found"));

        LessonBean lesson =
                lessonRepository.findById(
                        assignment.getLessonId()
                ).orElseThrow(
                        () -> new IllegalArgumentException("Lesson not found"));

        model.addAttribute( "assignment",assignment);
        model.addAttribute("batch",batch);
        model.addAttribute("lesson", lesson);
        return "assignment-detail";
    }
    
    @GetMapping("/assignments/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        AssignmentBean assignment = assignmentService.getAssignmentById(id);
        model.addAttribute("assignment",assignment);
        loadDropdownData(model);

        return "assignment-form";
    }

    @PostMapping("/assignments/update")
    public String updateAssignment(
            @ModelAttribute("assignment") AssignmentBean assignment,
            @RequestParam(value = "attachmentFile", required = false)
            MultipartFile attachmentFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            AssignmentBean existingAssignment =
                    assignmentService.getAssignmentById(
                            assignment.getAssignmentId());

            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                String fileUrl = saveAttachmentFile(attachmentFile);
                assignment.setAttachment(fileUrl);

            } else {
                assignment.setAttachment(existingAssignment.getAttachment());
            }
            assignmentService.updateAssignment( assignment);
            redirectAttributes.addFlashAttribute( "successMessage","Assignment updated successfully!");

            return "redirect:/teacher/assignments";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage",e.getMessage());
            loadDropdownData(model);
            return "assignment-form";

        } catch (IOException e) {
            model.addAttribute( "errorMessage","File upload failed: "
                            + e.getMessage());

            loadDropdownData(model);
            return "assignment-form";
        }
    }

    @PostMapping("/assignments/delete/{id}")
    public String deleteAssignment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {

            assignmentService.deleteAssignment(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Assignment deleted successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/teacher/assignments";
    }

    private String saveAttachmentFile( MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name" );
        }

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex).toLowerCase();
        }

        List<String> allowedExtensions =
                List.of(".pdf",".zip",".doc",".docx" );

        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException( "Only PDF, ZIP, DOC and DOCX files are allowed.");
        }

        String newFileName =UUID.randomUUID() .toString() + extension;
        Path filePath = uploadPath.resolve(newFileName);

        Files.copy(file.getInputStream(),filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/assignments/" + newFileName;
    }

    private void loadDropdownData(Model model) {
        List<BatchBean> batches = batchRepository.findAll();
        List<LessonBean> lessons = lessonRepository.findAll();
        
        model.addAttribute("batches", batches);
        model.addAttribute("lessons", lessons);
    }
    
    @GetMapping("/assignments/api/{id}")
    @ResponseBody
    public AssignmentBean getAssignmentApi(@PathVariable Integer id) {
        return assignmentService.getAssignmentById(id);
    }
}