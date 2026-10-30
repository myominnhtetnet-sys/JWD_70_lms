package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.Learning_Managnment_System.JWD_70_lms.Service.BatchDetailService;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchDetailBean.*;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/batches/{batchId}")
public class BatchDetailController {
	
	@Autowired
    private BatchDetailService batchDetailService;

    /**
     * 🟢 @ModelAttribute ဖြင့် Batch Detail Page များကို ဝင်ရောက်သည့်အခါတိုင်း 
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

    @GetMapping("/students")
    public String showStudentsPage(@PathVariable("batchId") Long batchId, Model model) {
        model.addAttribute("batchId", batchId);
        model.addAttribute("activeTab", "students");
        model.addAttribute("batches", batchDetailService.getAllBatches());
        model.addAttribute("lessons", batchDetailService.getAllLessons());
        return "Batch_Detail";
    }

    @GetMapping("/sessions")
    public String showSessionsPage(@PathVariable("batchId") Long batchId, Model model) {
        model.addAttribute("batchId", batchId);
        model.addAttribute("activeTab", "sessions");
        model.addAttribute("batches", batchDetailService.getAllBatches());
        model.addAttribute("lessons", batchDetailService.getAllLessons());
        return "Batch_Detail";
    }

    @GetMapping("/progress")
    public String showProgressPage(@PathVariable("batchId") Long batchId, Model model) {
        model.addAttribute("batchId", batchId);
        model.addAttribute("activeTab", "progress");
        model.addAttribute("batches", batchDetailService.getAllBatches());
        model.addAttribute("lessons", batchDetailService.getAllLessons());
        return "Batch_Detail";
    }

    @GetMapping("/announcements")
    public String showAnnouncementsPage(@PathVariable("batchId") Long batchId, Model model) {
        model.addAttribute("batchId", batchId);
        model.addAttribute("activeTab", "announcements");
        model.addAttribute("batches", batchDetailService.getAllBatches());
        model.addAttribute("lessons", batchDetailService.getAllLessons());
        return "Batch_Detail";
    }

    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<List<StudentInfo>> getBatchStudents(@PathVariable("batchId") Long batchId) {
        return ResponseEntity.ok(batchDetailService.getBatchStudents(batchId));
    }

    @GetMapping("/api/sessions")
    @ResponseBody
    public ResponseEntity<List<ClassSessionInfo>> getBatchSessions(@PathVariable("batchId") Long batchId) {
        return ResponseEntity.ok(batchDetailService.getBatchSessions(batchId));
    }

    @PostMapping("/api/sessions")
    @ResponseBody
    public ResponseEntity<?> createSession(@PathVariable("batchId") Long batchId, 
                                           @RequestBody ClassSessionInfo sessionInfo) {
        batchDetailService.saveClassSession(batchId, sessionInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/progress")
    @ResponseBody
    public ResponseEntity<List<StudentProgress>> getBatchStudentProgress(@PathVariable("batchId") Long batchId) {
        return ResponseEntity.ok(batchDetailService.getBatchStudentProgress(batchId));
    }

    @GetMapping("/api/announcements")
    @ResponseBody
    public ResponseEntity<List<AnnouncementInfo>> getBatchAnnouncements(@PathVariable("batchId") Long batchId) {
        return ResponseEntity.ok(batchDetailService.getBatchAnnouncements(batchId));
    }
}