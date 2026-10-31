package com.Learning_Managnment_System.JWD_70_lms.Controller;


import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipApplicationsRespority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @Autowired
    private ScholarshipApplicationsRespority scholarshipApplicationsRepository;

    @GetMapping({"/admin/reports", "/admin/report"})
    public String viewReports(Model model) {
        // Fetch all applications and pass them to report.html
        model.addAttribute("reports", scholarshipApplicationsRepository.findAll());
        return "report"; 
    }
}