package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @GetMapping({"/admin/reports", "/admin/report"})
    public String viewReports() {
        return "report"; 
    }
}