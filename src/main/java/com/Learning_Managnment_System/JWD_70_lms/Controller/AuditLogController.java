package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private boolean isNotAdmin(HttpSession session) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        Object roleIdObj = session.getAttribute("roleId");
        
        int roleId = 0;
        if (currentUser != null) {
            roleId = currentUser.getRole_id();
        } else if (roleIdObj != null) {
            roleId = (int) roleIdObj;
        }
        
        return roleId != 1; 
    }

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

    @GetMapping("/admin/audit-logs")
    public String showAuditLogPage(HttpSession session, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("Audit Logs", "Audit logs");
        return "Audit_Logs"; // Audit_Logs.html View သို့ သွားမည်
    }
    
    @GetMapping("/api/audit-logs")
    @ResponseBody
    public ResponseEntity<?> getAllAuditLogs(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        List<AuditLogBean> logs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/api/audit-logs/action/{actionType}")
    @ResponseBody
    public ResponseEntity<?> getLogsByActionType(@PathVariable String actionType, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        List<AuditLogBean> logs = auditLogService.getLogsByActionType(actionType);
        return ResponseEntity.ok(logs);
    }
}