package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor; // ADD THIS
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;                // ADD THIS
import org.springframework.web.bind.annotation.*;

import com.Learning_Managnment_System.JWD_70_lms.Service.CourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ADD THIS METHOD: It automatically turns empty form boxes into null values!
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/show")
    public String showAndFilterCourses(@ModelAttribute CourseBean filters, Model model) {
        // Runs your clean dynamic specifications query
        List<CourseBean> courses = courseService.filterCourses(filters);
        
        model.addAttribute("list", courses);
        return "courses"; 
    }
}
