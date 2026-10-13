package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.CategoryService;
import com.Learning_Managnment_System.JWD_70_lms.Service.CourseFileUploadService;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final CourseFileUploadService fileUploadService;

    public CourseController(CourseRepository courseRepository, CategoryService categoryService, CourseFileUploadService fileUploadService) {
        this.courseRepository = courseRepository;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping
    public String courseListPage() {
        return "Course_Management"; 
    }

    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<CourseBean>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<CategoryBean>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getCourseById(@PathVariable Integer id) {
        CourseBean course = courseRepository.findById(id);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Course not found with ID: " + id));
    }

    // 🟢 CREATE COURSE
    @PostMapping(value = "/api/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> createCourse(
            @RequestPart("course") CourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileUploadService.saveFile(imageFile);
                course.setThumbnail(fileName);
            }

            if (course.getLanguage() == null || course.getLanguage().trim().isEmpty()) {
                course.setLanguage("Java");
            }
            if (course.getStatus() == null || course.getStatus().trim().isEmpty()) {
                course.setStatus("DRAFT");
            }
            if (course.getCreated_by() == null) {
                course.setCreated_by(1);
            }

            courseRepository.save(course);
            return ResponseEntity.ok(Map.of("message", "Course created successfully!"));
        } catch (IllegalArgumentException e) {
            // Validation Error (2MB ကျော်ခြင်း သို့မဟုတ် Image မဟုတ်ခြင်း)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 🟢 UPDATE COURSE
    @PutMapping(value = "/api/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateCourse(
            @RequestPart("course") CourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileUploadService.saveFile(imageFile);
                course.setThumbnail(fileName);
            }

            courseRepository.update(course);
            return ResponseEntity.ok(Map.of("message", "Course updated successfully!"));
        } catch (IllegalArgumentException e) {
            // Validation Error (2MB ကျော်ခြင်း သို့မဟုတ် Image မဟုတ်ခြင်း)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        try {
            courseRepository.softDelete(id);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}