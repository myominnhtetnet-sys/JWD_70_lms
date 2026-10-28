package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.Learning_Managnment_System.JWD_70_lms.Service.AdminCourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminCourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean; // Falls Kategorien-Modell existéiert, soss unpassen
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {
    
    @Autowired
    private AdminCourseService courseService;

    // Upload-Verzeechnes (Dir kënnt et un äre Projet upassen)
    private static final String UPLOAD_DIR = "uploads/";

    // 1. HTML View Page op maachen
    @GetMapping
    public String getCourseListPage(Model model) {
        return "Course_Management"; 
    }

    // 2. API: All Courses Lëscht als JSON zeréckginn (Matchéiert mat fetch('/admin/courses/api/list'))
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<AdminCourseBean>> getAllCoursesApi() {
        List<AdminCourseBean> courses = courseService.getAllCoursesForAdmin(); // Oder d'Method déi all Coursen zielt
        return ResponseEntity.ok(courses);
    }

    // 3. API: Kategorien lëschten (Matchéiert mat fetch('/admin/courses/api/categories'))
    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<CategoryBean>> getCategoriesApi() {
        // Hei gëtt ugeholl datt Dir eng Service-Method hutt fir Kategorien ze kréien
        List<CategoryBean> categories = courseService.getAllCategories(); 
        return ResponseEntity.ok(categories);
    }

    // 4. API: Eenzelne Cours no ID kréien (Fir View / Edit)
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<AdminCourseBean> getCourseByIdApi(@PathVariable("id") Integer id) {
        System.out.println("Received Request for Course ID: " + id); // Log ထည့်စစ်ရန်
        AdminCourseBean course = courseService.getCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        System.out.println("Course not found in DB for ID: " + id);
        return ResponseEntity.notFound().build();
    }

    // 5. API: Cours derbäisetzen (Create mat JSON an optional Bild-Upload)
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createCourseApi(
            @RequestPart("course") AdminCourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                course.setThumbnail(fileName);
            }

            courseService.saveCourse(course);
            return ResponseEntity.ok().body("{\"message\": \"Course created successfully\"}");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to upload image: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

 // 6. API: Cours updaten (Update mat JSON an optional Bild-Upload)
    @PutMapping("/api/update")
    @ResponseBody
    public ResponseEntity<?> updateCourseApi(
            @RequestPart("course") AdminCourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // Category ID လွတ်နေသလား စစ်ဆေးရန်
            if (course.getCategoryId() == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"Category ID cannot be null\"}");
            }

            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                course.setThumbnail(fileName);
            }

            courseService.updateCourse(course);
            return ResponseEntity.ok().body("{\"message\": \"Course updated successfully\"}");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to upload image: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    // 7. API: Cours läschen (Soft Delete oder Delete)
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourseApi(@PathVariable("id") Integer id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().body("{\"message\": \"Course deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}