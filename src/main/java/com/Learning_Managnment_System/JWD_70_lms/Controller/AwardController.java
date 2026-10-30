package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.AwardService;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student/awards")
public class AwardController {

    @Autowired
    private AwardService awardService;

    @GetMapping
    public String showAwardsDashboard(Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        List<StudentGradeBean> awardsList = awardService.getStudentAwardsDashboard(studentEmail);
        model.addAttribute("awards", awardsList);
        model.addAttribute("fullName", session.getAttribute("fullName")); 
        
        return "student_awards_dashboard";
    }

    @GetMapping("/certificate/{id}")
    public String viewPrintableCertificate(@PathVariable("id") int certificateId, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        StudentGradeBean diploma = awardService.getPrintableCertificate(certificateId, studentEmail);
        if (diploma == null) {
            return "redirect:/student/awards";
        }

        model.addAttribute("diploma", diploma);
        return "view_award_diploma";
    }
}
