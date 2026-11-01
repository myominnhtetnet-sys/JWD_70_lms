package com.Learning_Managnment_System.JWD_70_lms.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Learning_Managnment_System.JWD_70_lms.Repository.TeacherCourseBuilderRepository;

import java.util.List;
import java.util.Map;

@Controller
public class TeacherCourseBuilderController {

    @Autowired
    private TeacherCourseBuilderRepository courseBuilderRepository;

    @GetMapping("/teacher/courses/{id}/builder")
    public String viewCourseBuilder(
            @PathVariable("id") int courseId,
            @RequestParam(value = "moduleId", required = false) Integer selectedModuleId,
            HttpSession session,
            Model model) {

        // Check if user is logged in as teacher
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }

        // Fetch course info
        Map<String, Object> course = courseBuilderRepository.getCourseById(courseId);
        if (course == null) {
            return "redirect:/teacher/courses";
        }

        // Fetch modules
        List <Map<String, Object>> modules = courseBuilderRepository.getModulesByCourseId(courseId);

        // Default to first module if none selected
        if (selectedModuleId == null && !modules.isEmpty()) {
            Object moduleIdObj = modules.get(0).get("module_id");
            if (moduleIdObj != null) {
                selectedModuleId = ((Number) moduleIdObj).intValue();
            }
        }

        // Fetch lessons for selected module
        List <Map<String, Object>> lessons = selectedModuleId != null ? 
        		courseBuilderRepository.getLessonsByModuleId(selectedModuleId) : List.of();

        // Fetch course materials
        List <Map<String, Object>> materials = courseBuilderRepository.getMaterialsByCourseId(courseId);

        // Pass attributes to model
        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("course", course);
        model.addAttribute("modules", modules);
        model.addAttribute("lessons", lessons);
        model.addAttribute("materials", materials);
        model.addAttribute("selectedModuleId", selectedModuleId);

        return "teacher_course_builder";
    }

 // Handle adding a new module with moduleError flash attribute
    @PostMapping("/teacher/courses/{id}/modules/add")
    public String addModule(
            @PathVariable("id") int courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortOrder", defaultValue = "1") int sortOrder,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        
        if (sortOrder < 1) {
            redirectAttributes.addFlashAttribute("moduleError", "Sort order must be 1 or greater!");
            return "redirect:/teacher/courses/" + courseId + "/builder";
        }
        
        try {
            courseBuilderRepository.insertModule(courseId, title, description, sortOrder);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("moduleError", "Module with sort order " + sortOrder + " already exists!");
        }
        return "redirect:/teacher/courses/" + courseId + "/builder";
    }
    
    // Handle deleting a module
    @GetMapping("/teacher/courses/{id}/modules/delete/{moduleId}")
    public String deleteModule(
            @PathVariable("id") int courseId,
            @PathVariable("moduleId") int moduleId,
            HttpSession session) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        courseBuilderRepository.deleteModule(moduleId);
        return "redirect:/teacher/courses/" + courseId + "/builder";
    }

 // Handle adding a new lesson with content-type URL validation
    @PostMapping("/teacher/courses/{id}/lessons/add")
    public String addLesson(
            @PathVariable("id") int courseId,
            @RequestParam("moduleId") int moduleId,
            @RequestParam("title") String title,
            @RequestParam(value = "contentType", defaultValue = "VIDEO") String contentType,
            @RequestParam(value = "durationMin", defaultValue = "15") int durationMin,
            @RequestParam(value = "status", defaultValue = "DRAFT") String status,
            @RequestParam(value = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(value = "isPreview", defaultValue = "0") int isPreview,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "content", required = false) String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        // Validate sort order is greater than 0
        if (sortOrder < 1) {
            redirectAttributes.addFlashAttribute("lessonError", "Lesson sort order must be 1 or greater!");
            return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
        }

        // Check for duplicate sort order in the same module
        if (courseBuilderRepository.lessonSortOrderExists(moduleId, sortOrder)) {
            redirectAttributes.addFlashAttribute("lessonError", "Lesson with sort order " + sortOrder + " already exists in this module!");
            return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
        }

        // Validate URL mismatch: Text or PDF cannot use an .mp4 video URL
        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            String lowerUrl = videoUrl.toLowerCase();
            if (("PDF".equals(contentType) || "TEXT".equals(contentType)) && lowerUrl.contains(".mp4")) {
                redirectAttributes.addFlashAttribute("lessonError", "A " + contentType + " lesson cannot use an .mp4 video URL!");
                return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
            }
        }

        courseBuilderRepository.insertLesson(moduleId, title, contentType, content, videoUrl, durationMin, sortOrder, isPreview, status);
        return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
    }

    // Handle updating an existing lesson with content-type URL validation
    @PostMapping("/teacher/courses/{id}/lessons/update/{lessonId}")
    public String updateLesson(
            @PathVariable("id") int courseId,
            @PathVariable("lessonId") int lessonId,
            @RequestParam("moduleId") int moduleId,
            @RequestParam("title") String title,
            @RequestParam(value = "contentType", defaultValue = "VIDEO") String contentType,
            @RequestParam(value = "durationMin", defaultValue = "15") int durationMin,
            @RequestParam(value = "status", defaultValue = "DRAFT") String status,
            @RequestParam(value = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(value = "isPreview", defaultValue = "0") int isPreview,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "content", required = false) String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        if (sortOrder < 1) {
            redirectAttributes.addFlashAttribute("lessonError", "Lesson sort order must be 1 or greater!");
            return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
        }

        // Validate URL mismatch for updates too: Text or PDF cannot use an .mp4 video URL
        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            String lowerUrl = videoUrl.toLowerCase();
            if (("PDF".equals(contentType) || "TEXT".equals(contentType)) && lowerUrl.contains(".mp4")) {
                redirectAttributes.addFlashAttribute("lessonError", "A " + contentType + " lesson cannot use an .mp4 video URL!");
                return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
            }
        }

        courseBuilderRepository.updateLesson(lessonId, title, contentType, content, videoUrl, durationMin, sortOrder, isPreview, status);
        return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
    }
 // Handle deleting a lesson
    @GetMapping("/teacher/courses/{id}/lessons/delete/{lessonId}")
    public String deleteLesson(
            @PathVariable("id") int courseId,
            @PathVariable("lessonId") int lessonId,
            @RequestParam("moduleId") int moduleId,
            HttpSession session) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        
        courseBuilderRepository.deleteLesson(lessonId);
        return "redirect:/teacher/courses/" + courseId + "/builder?moduleId=" + moduleId;
    }
 // Handle uploading a course material file from desktop
    @PostMapping("/teacher/courses/{id}/materials/upload")
    public String uploadMaterial(
            @PathVariable("id") int courseId,
            @RequestParam("title") String title,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/teacher/courses/" + courseId + "/builder?tab=materials";
        }

        try {
            String uploadDir = "uploads/materials/";
            java.io.File uploadPath = new java.io.File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + uniqueFilename);
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String fileType = "FILE";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase();
            }

            String fileUrl = "/uploads/materials/" + uniqueFilename;

            courseBuilderRepository.insertMaterial(courseId, title, fileType, fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file: " + e.getMessage());
        }

        // Redirect back directly to the materials tab
        return "redirect:/teacher/courses/" + courseId + "/builder?tab=materials";
    }
 // Handle deleting a course material file
    @GetMapping("/teacher/courses/{id}/materials/delete/{materialId}")
    public String deleteMaterial(
            @PathVariable("id") int courseId,
            @PathVariable("materialId") int materialId,
            HttpSession session) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        
        courseBuilderRepository.deleteMaterial(materialId);
        return "redirect:/teacher/courses/" + courseId + "/builder?tab=materials";
    }
}