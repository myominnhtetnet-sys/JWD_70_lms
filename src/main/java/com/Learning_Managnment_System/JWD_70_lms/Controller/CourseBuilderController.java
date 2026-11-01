package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseBuilderService;
import com.Learning_Managnment_System.JWD_70_lms.model.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/course-builder")
public class CourseBuilderController {
	
    @Autowired
    private CourseBuilderService courseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private boolean isNotAdmin(HttpSession session) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        Object roleIdObj = session.getAttribute("roleId");
        int roleId = currentUser != null ? currentUser.getRole_id() : (roleIdObj != null ? (int) roleIdObj : 0);
        return roleId != 1; 
    }
    
    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        String displayName = "Administrator";
        int roleId = 1;
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            displayName = currentUser.getFull_name() != null ? currentUser.getFull_name() : displayName;
            roleId = currentUser.getRole_id();
        } else {
            if (session.getAttribute("fullName") != null) displayName = (String) session.getAttribute("fullName");
            if (session.getAttribute("roleId") != null) roleId = (int) session.getAttribute("roleId");
        }
        model.addAttribute("fullName", displayName);
        model.addAttribute("loggedInRole", roleId == 1 ? "Administrator" : (roleId == 2 ? "Instructor" : "Student"));
    }

    @GetMapping
    public String showCourseListPage(HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/login";
        List<CourseBuilderBean> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("course", courses != null && !courses.isEmpty() ? courses.get(0) : new CourseBuilderBean());
        model.addAttribute("activeTab", "course-builder"); 
        return "Course_Builder"; 
    }

    @GetMapping("/{id}/builder")
    public String showCourseBuilderDetail(HttpSession session, @PathVariable Long id, Model model) {
        if (isNotAdmin(session)) return "redirect:/login";
        CourseBuilderBean course = courseService.getCourseById(id);
        if (course == null) return "redirect:/admin/course-builder";
        model.addAttribute("course", course);
        model.addAttribute("activeTab", "course-builder");
        return "Course_Builder_Detail"; 
    }

    @GetMapping("/api")
    @ResponseBody
    public List<CourseBuilderBean> getCoursesApi() { return courseService.getAllCourses(); }

    // 🟢 1. API: Get Single Course by ID for Edit Modal
    @GetMapping("/api/single/{id}")
    @ResponseBody
    public ResponseEntity<?> getSingleCourseApi(@PathVariable Long id) {
        CourseBuilderBean course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public List<?> getCategoriesApi() { return jdbcTemplate.queryForList("SELECT category_id, name FROM categories"); }

    @GetMapping("/api/instructors")
    @ResponseBody
    public List<InstructorBean> getInstructorsApi() { return courseService.getAllInstructors(); }

    // --- 1. COURSE INSTRUCTORS APIS ---
    @GetMapping("/api/{courseId}/instructors")
    @ResponseBody
    public List<InstructorBean> getCourseInstructorsApi(@PathVariable Long courseId) { return courseService.getCourseInstructors(courseId); }

    @PostMapping("/api/instructors")
    @ResponseBody
    public ResponseEntity<?> assignInstructorApi(@RequestBody Map<String, Object> payload) {
        try {
            Long courseId = Long.valueOf(payload.get("courseId").toString());
            Long instructorId = Long.valueOf(payload.get("instructorId").toString());
            courseService.assignInstructor(courseId, instructorId);
            return ResponseEntity.ok().body("Instructor assigned successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    @DeleteMapping("/api/{courseId}/instructors/{instructorId}")
    @ResponseBody
    public ResponseEntity<?> removeInstructorApi(@PathVariable Long courseId, @PathVariable Long instructorId) {
        try {
            courseService.removeInstructor(courseId, instructorId);
            return ResponseEntity.ok().body("Instructor removed successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    // --- 2. COURSE MODULES APIS ---
    @GetMapping("/api/{courseId}/modules")
    @ResponseBody
    public List<ModuleBean> getModulesApi(@PathVariable Long courseId) { return courseService.getModules(courseId); }

    @PostMapping("/api/modules")
    @ResponseBody
    public ResponseEntity<?> createModuleApi(@RequestBody Map<String, Object> payload) {
        try {
            Long courseId = Long.valueOf(payload.get("courseId").toString());
            String title = payload.get("title").toString();
            courseService.createModule(courseId, title);
            return ResponseEntity.ok().body("Module created successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    @DeleteMapping("/api/modules/{moduleId}")
    @ResponseBody
    public ResponseEntity<?> deleteModuleApi(@PathVariable Long moduleId) {
        try {
            courseService.deleteModule(moduleId);
            return ResponseEntity.ok().body("Module deleted successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    // --- 3. LESSONS APIS ---
    @GetMapping("/api/{courseId}/lessons")
    @ResponseBody
    public List<LessonBean> getLessonsApi(@PathVariable Long courseId) { return courseService.getLessons(courseId); }

    @PostMapping("/api/lessons")
    @ResponseBody
    public ResponseEntity<?> createLessonApi(@RequestBody Map<String, Object> payload) {
        try {
            Long courseId = Long.valueOf(payload.get("courseId").toString());
            String title = payload.get("title").toString();
            String url = payload.getOrDefault("url", "").toString();
            courseService.createLesson(courseId, title, url);
            return ResponseEntity.ok().body("Lesson created successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    @ResponseBody
    public ResponseEntity<?> deleteLessonApi(@PathVariable Long lessonId) {
        try {
            courseService.deleteLesson(lessonId);
            return ResponseEntity.ok().body("Lesson deleted successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    // --- 4. MATERIALS APIS ---
    @GetMapping("/api/{courseId}/materials")
    @ResponseBody
    public List<MaterialBean> getMaterialsApi(@PathVariable Long courseId) { return courseService.getMaterials(courseId); }

    @PostMapping("/api/materials")
    @ResponseBody
    public ResponseEntity<?> createMaterialApi(@RequestBody Map<String, Object> payload) {
        try {
            Long courseId = Long.valueOf(payload.get("courseId").toString());
            String title = payload.get("title").toString();
            String filePath = payload.get("filePath").toString();
            courseService.createMaterial(courseId, title, filePath);
            return ResponseEntity.ok().body("Material uploaded successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    @DeleteMapping("/api/materials/{materialId}")
    @ResponseBody
    public ResponseEntity<?> deleteMaterialApi(@PathVariable Long materialId) {
        try {
            courseService.deleteMaterial(materialId);
            return ResponseEntity.ok().body("Material deleted successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed: " + e.getMessage()); }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createCourseApi(@RequestBody CourseBuilderBean course) {
        try {
            courseService.createCourse(course, 1);
            return ResponseEntity.ok().body("Course created successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed to create course: " + e.getMessage()); }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourseApi(@PathVariable Long id, @RequestBody CourseBuilderBean course) {
        try {
            courseService.updateCourse(id, course);
            return ResponseEntity.ok().body("Course updated successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed to update course: " + e.getMessage()); }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourseApi(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().body("Course deleted successfully");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Failed to delete course: " + e.getMessage()); }
    }
}