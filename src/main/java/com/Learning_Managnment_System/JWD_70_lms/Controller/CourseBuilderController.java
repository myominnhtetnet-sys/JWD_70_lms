package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.jdbc.core.JdbcTemplate;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseBuilderService;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBuilderBean;

@Controller
@RequestMapping("/admin/course-builder")
public class CourseBuilderController {
	
    @Autowired
    private CourseBuilderService courseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Course List Page
    @GetMapping
    public String showCourseListPage(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("activeTab", "course-biulder"); 
        return "Course_Builder"; 
    }

    // Course Builder Detail Page
    @GetMapping("/{id}/builder")
    public String showCourseBuilderDetail(@PathVariable Long id, Model model) {
        CourseBuilderBean course = courseService.getCourseById(id);
        if (course == null) {
            return "redirect:/admin/course-builder";
        }
        model.addAttribute("course", course);
        model.addAttribute("activeTab", "course-biulder");
        return "Course_Builder_Detail"; 
    }

    // API: Get All Courses (JSON)
    @GetMapping("/api")
    @ResponseBody
    public List<CourseBuilderBean> getCoursesApi() {
        return courseService.getAllCourses();
    }

    // API: Get All Categories for Dropdown
    @GetMapping("/api/categories")
    @ResponseBody
    public List<?> getCategoriesApi() {
        String sql = "SELECT category_id, name FROM categories";
        return jdbcTemplate.queryForList(sql);
    }

    // API: Create Course
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createCourseApi(@RequestBody CourseBuilderBean course) {
        try {
            int currentUserId = 1;
            courseService.createCourse(course, currentUserId);
            return ResponseEntity.ok().body("Course created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create course: " + e.getMessage());
        }
    }

    // API: Update Course
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourseApi(@PathVariable Long id, @RequestBody CourseBuilderBean course) {
        try {
            courseService.updateCourse(id, course);
            return ResponseEntity.ok().body("Course updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update course: " + e.getMessage());
        }
    }

    // API: Delete Course
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourseApi(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().body("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete course. It may be linked to active batches or modules.");
        }
    }
}