package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.Learning_Managnment_System.JWD_70_lms.Service.AnnouncementService;
import com.Learning_Managnment_System.JWD_70_lms.Service.BatchService;
import com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.BatchInfo;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "*")
public class AnnouncementController {
	
    @Autowired
    private AnnouncementService announcementService;
	
    @Autowired
    private BatchService batchService;

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

    @GetMapping("/admin/announcements")
    public String showAnnouncementsPage(HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        return "Announcements_Management"; 
    }

    @GetMapping("/api/announcements")
    @ResponseBody
    public ResponseEntity<?> getAll(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/api/announcements/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        AnnouncementBean announcement = announcementService.getAnnouncementById(id);
        if (announcement != null) {
            return ResponseEntity.ok(announcement);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/announcements")
    @ResponseBody
    public ResponseEntity<String> create(@RequestBody AnnouncementBean announcement, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        boolean isCreated = announcementService.createAnnouncement(announcement);
        if (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Saved successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to save.");
    }

    @PutMapping("/api/announcements/{id}")
    @ResponseBody
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody AnnouncementBean announcement, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        boolean isUpdated = announcementService.updateAnnouncement(id, announcement);
        if (isUpdated) {
            return ResponseEntity.ok("Updated successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/announcements/{id}")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Long id, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }
        boolean isDeleted = announcementService.deleteAnnouncement(id);
        if (isDeleted) {
            return ResponseEntity.ok("Deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/announcements/batches")
    @ResponseBody
    public ResponseEntity<?> getActiveBatchesApi(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        List<BatchInfo> batches = batchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/api/announcements/latest")
    @ResponseBody
    public ResponseEntity<?> getLatestAnnouncements(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthorized access\"}");
        }
        List<AnnouncementBean> latestList = announcementService.getLatestAnnouncements(2);
        return ResponseEntity.ok(latestList);
    }
}