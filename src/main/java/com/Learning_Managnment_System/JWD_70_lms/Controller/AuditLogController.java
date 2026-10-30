package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Learning_Managnment_System.JWD_70_lms.Service.AuditLogService;
import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;

@Controller
public class AuditLogController {
	
	@Autowired
    private AuditLogService auditLogService;

    // 1. HTML Page Render ပြုလုပ်ရန် (URL: /admin/audit-logs)
    @GetMapping("/admin/audit-logs")
    public String showAuditLogPage(Model model) {
    	model.addAttribute("Audit Logs", "Audit logs");
        return "Audit_Logs"; // audit-logs.html View သို့ သွားမည်
    }

    // 2. JavaScript Fetch ခေါ်ယူသည့် REST API (URL: /api/audit-logs)
    @GetMapping("/api/audit-logs")
    @ResponseBody
    public List<AuditLogBean> getAllAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }

    // 3. Action Filter အလိုက် JSON Data ပြန်ပေးသည့် API (URL: /api/audit-logs/action/{actionType})
    @GetMapping("/api/audit-logs/action/{actionType}")
    @ResponseBody
    public List<AuditLogBean> getLogsByActionType(@PathVariable String actionType) {
        return auditLogService.getLogsByActionType(actionType);
    }
}