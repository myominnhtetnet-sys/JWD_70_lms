package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Teacher_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AttendanceBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
public class Teacher_Controller {

    @Autowired
    private Teacher_Repository teacherRepository;

	/* private final String UPLOAD_DIR = "uploads/materials/"; */

    private boolean isNotTeacher(HttpSession session) {
        Object roleId = session.getAttribute("roleId");
        return roleId == null || (int) roleId != 2;
    }

    private void addCommonAttributes(HttpSession session, Model model) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("batches", teacherRepository.getBatchesByInstructor(instructorId));
        model.addAttribute("courses", teacherRepository.getCoursesByInstructor(instructorId));
    }

    // 1. Dashboard Route
    @GetMapping({"", "/", "/dashboard"})
    public String teacherDashboard(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        model.addAttribute("activeView", "dashboard");
        return "teacher"; 
    }

    // 2. Courses Route
    @GetMapping("/courses")
    public String myCourses(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        model.addAttribute("activeView", "courses");
        return "teacher"; 
    }
    
	/*
	 * @GetMapping("/courses/{courseId}/builder") public String
	 * courseBuilder(@PathVariable("courseId") Long courseId, Model model) { // Make
	 * sure you pass a valid lessonId or list of lessons to the model!
	 * model.addAttribute("activeLessonId", 1L); // Example hardcoded or fetched ID
	 * for testing model.addAttribute("materials",
	 * teacherRepository.getMaterialsByLesson(1L)); return "course_builder"; }
	 */

    // 3. Batches Route
    @GetMapping("/batches")
    public String myBatches(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        model.addAttribute("activeView", "batches");
        return "teacher";
    }

    // 3.1 Batch Detail Route
    @GetMapping("/batches/{id}")
    public String batchDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        model.addAttribute("batchId", id);
        model.addAttribute("batch", teacherRepository.getBatchById(id));
        model.addAttribute("students", teacherRepository.getStudentsByBatch(id));
        model.addAttribute("activeView", "batch-detail"); 
        
        return "teacher";
    }

    // 4. Attendance Route
    @GetMapping("/attendance")
    public String takeAttendance(@RequestParam(value = "batchId", required = false) Long batchId, 
                                 @RequestParam(value = "sessionId", required = false) Long sessionId,
                                 HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("selectedSessionId", sessionId); // <-- Pass selected session ID to model

        if (batchId != null) {
            model.addAttribute("sessions", teacherRepository.getSessionsByBatch(batchId)); // <-- Loads session dates
            model.addAttribute("students", teacherRepository.getRosterByBatch(batchId));
        } else {
            model.addAttribute("sessions", Collections.emptyList());
            model.addAttribute("students", Collections.emptyList());
        }

        model.addAttribute("activeView", "attendance");
        return "teacher"; 
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@ModelAttribute("attendanceForm") AttendanceBean form, 
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTeacher(session)) return "redirect:/login";

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int teacherId = currentUser != null ? currentUser.getUser_id() : 2;

        // Ensure session ID and attendance list are present before saving
        if (form.getSessionId() != null && form.getAttendanceList() != null) {
            for (var item : form.getAttendanceList()) {
                // Pass form.getSessionId() instead of hardcoded 1
                teacherRepository.saveAttendance(form.getSessionId(), item.getEnrollmentId(), item.getStatus(), teacherId);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Attendance saved successfully!");
        return "redirect:/teacher/attendance?batchId=" + form.getSessionId(); // Keeps the view active
    }
	/*
	 * @PostMapping("/lessons/{lessonId}/materials/upload") public String
	 * uploadLessonMaterial(@PathVariable("lessonId") Long lessonId,
	 * 
	 * @RequestParam("title") String title,
	 * 
	 * @RequestParam("file") MultipartFile file, HttpSession session,
	 * RedirectAttributes redirectAttributes) { if (isNotTeacher(session)) return
	 * "redirect:/login";
	 * 
	 * try { if (!file.isEmpty()) { Path uploadPath =
	 * Paths.get("uploads/materials/"); if (!Files.exists(uploadPath)) {
	 * Files.createDirectories(uploadPath); }
	 * 
	 * String originalFileName = file.getOriginalFilename(); String uniqueFileName =
	 * UUID.randomUUID().toString() + "_" + originalFileName; Path filePath =
	 * uploadPath.resolve(uniqueFileName);
	 * 
	 * Files.copy(file.getInputStream(), filePath);
	 * 
	 * // Calculate file size in KB long fileSizeKb = file.getSize() / 1024; String
	 * fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")
	 * + 1).toUpperCase();
	 * 
	 * // Save to existing lesson_materials table
	 * teacherRepository.saveLessonMaterial(lessonId, title, uniqueFileName,
	 * fileExtension, fileSizeKb); redirectAttributes.addFlashAttribute("success",
	 * "Lesson material uploaded successfully!"); } } catch (Exception e) {
	 * e.printStackTrace(); redirectAttributes.addFlashAttribute("error",
	 * "Upload failed: " + e.getMessage()); }
	 * 
	 * return "redirect:/teacher/lessons/" + lessonId; } // Serve / Preview Uploaded
	 * Files directly in the browser (PDF, images, text, etc.)
	 * 
	 * @GetMapping("/materials/preview/{fileName}") public ResponseEntity<Resource>
	 * previewFile(@PathVariable("fileName") String fileName) { try { Path filePath
	 * = Paths.get(UPLOAD_DIR).resolve(fileName).normalize(); Resource resource =
	 * new UrlResource(filePath.toUri());
	 * 
	 * if (resource.exists() || resource.isReadable()) { // 'inline' tells the
	 * browser to display the file instead of downloading it directly return
	 * ResponseEntity.ok() .header(HttpHeaders.CONTENT_DISPOSITION,
	 * "inline; filename=\"" + resource.getFilename() + "\"") .body(resource); } }
	 * catch (Exception e) { e.printStackTrace(); } return
	 * ResponseEntity.notFound().build(); }
	 */
}