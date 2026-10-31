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
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/courses")
public class AdminCourseController {
	
	@Autowired
    private AdminCourseService courseService;

    // Upload Directory
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 🟢 @ModelAttribute ဖြင့် Course Management စာမျက်နှာသို့ ဝင်ရောက်သည့်အခါတိုင်း 
     * Login ဝင်ထားသော User ၏ အမည်နှင့် Role ကို Header ထဲသို့ အလိုအလျောက် ပို့ပေးခြင်း
     */
    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        String displayName = "Administrator";
        int roleId = 1;

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            if (currentUser.getFull_name() != null) {
                displayName = currentUser.getFull_name();
            }
            roleId = currentUser.getRole_id();
        } else {
            if (session.getAttribute("fullName") != null) {
                displayName = (String) session.getAttribute("fullName");
            }
            if (session.getAttribute("roleId") != null) {
                roleId = (int) session.getAttribute("roleId");
            }
        }

        String roleName = "Administrator";
        if (roleId == 1) {
            roleName = "Administrator";
        } else if (roleId == 2) {
            roleName = "Instructor";
        } else if (roleId == 3) {
            roleName = "Student";
        }

        model.addAttribute("fullName", displayName);
        model.addAttribute("loggedInRole", roleName);
    }

    // 1. HTML View Page
    @GetMapping
    public String getCourseListPage(Model model) {
        return "Course_Management"; 
    }

    // 2. API: All Courses List as JSON
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<AdminCourseBean>> getAllCoursesApi() {
        List<AdminCourseBean> courses = courseService.getAllCoursesForAdmin();
        return ResponseEntity.ok(courses);
    }

    // 3. API: Categories list
    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<CategoryBean>> getCategoriesApi() {
        List<CategoryBean> categories = courseService.getAllCategories(); 
        return ResponseEntity.ok(categories);
    }

    // 4. API: Get Single Course by ID (For View / Edit)
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<AdminCourseBean> getCourseByIdApi(@PathVariable("id") Integer id) {
        System.out.println("Received Request for Course ID: " + id);
        AdminCourseBean course = courseService.getCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        System.out.println("Course not found in DB for ID: " + id);
        return ResponseEntity.notFound().build();
    }

    // 5. API: Create Course (with JSON and optional image upload)
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

    // 6. API: Update Course (with JSON and optional image upload)
    @PutMapping("/api/update")
    @ResponseBody
    public ResponseEntity<?> updateCourseApi(
            @RequestPart("course") AdminCourseBean course,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
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

    // 7. API: Delete Course
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