package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/show")
    public String showAndFilterCourses(
            @ModelAttribute CourseBean filters, 
            @RequestParam(value = "page", defaultValue = "0") int page, // Captures current active page index
            Model model) {
        
        // Enforce exactly 6 items per page requirement
        Pageable pageable = PageRequest.of(page, 6);
        
        // Update your service layer to return a org.springframework.data.domain.Page object
        Page<CourseBean> coursePage = courseService.filterCourses(filters, pageable);
        
        // Expose dynamic pagination structural metadata elements to your Thymeleaf template
        model.addAttribute("list", coursePage.getContent());              // Only contains the 6 items for this page
        model.addAttribute("currentPage", page);                           // Tracks active page highlight state
        model.addAttribute("totalPages", coursePage.getTotalPages());      // Tracks layout boundaries
        
        return "courses"; 
    }
    
    @GetMapping("/detail/{id}")
    public String showCourseDetail(@PathVariable("id") int id, Model model) {
        // Fetch the single target record from your backend storage layer
        // Example: CourseBean course = courseService.findById(id);
        CourseBean course = courseService.getCourseById(id); 
        
        if (course == null) {
            return "redirect:/courses/show"; // Safe structural fallback redirect
        }
        
        model.addAttribute("course", course);
        return "course-detail"; // Directs to your new comprehensive view file
    }
       
   @PostMapping("/{id}/review")
    public String saveCourseReview(
            @PathVariable("id") Long courseId,
            @RequestParam("userId") Long userId, // Captures primary key matching your users schema
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment) {
            
        // Execute target query mapping record logic inside your service layer 
        // e.g., courseService.addReview(courseId, userId, rating, comment);
        
        // Auto-refresh layout while holding the current active modal tab anchor view open
        return "redirect:/courses/show#detailModal-" + courseId;
    }


}
