package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import com.Learning_Managnment_System.JWD_70_lms.Service.AuditLogService;
import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

@Controller
public class AuditLogController {
	
	@Autowired
    private AuditLogService auditLogService;

    // 🟢 Login ဝင်ထားသော User ၏ အမည်နှင့် Role ကို Header ထဲသို့ အလိုအလျောက် ပို့ပေးခြင်း
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