package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Learning_Managnment_System.JWD_70_lms.Service.BatchService;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.*;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/batches-manage")
public class BatchController {
	
	private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /**
     * 🟢 @ModelAttribute ဖြင့် Batch Management စာမျက်နှာများသို့ ဝင်ရောက်သည့်အခါတိုင်း 
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

    @GetMapping
    public String showBatchManagePage(Model model) {
        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("activeTab", "batches_manage");
        return "Batch_Create"; // Template နာမည်ကို Batch_Management သို့ ပြန်ပြင်ထားသည်
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<BatchInfo>> getAllBatchesApi() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createBatch(@RequestBody BatchInfo batchInfo) {
        batchService.createBatch(batchInfo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/{batchId}")
    @ResponseBody
    public ResponseEntity<?> updateBatch(@PathVariable("batchId") Long batchId, @RequestBody BatchInfo batchInfo) {
        batchService.updateBatch(batchId, batchInfo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/{batchId}")
    @ResponseBody
    public ResponseEntity<?> deleteBatch(@PathVariable("batchId") Long batchId) {
        try {
            batchService.deleteBatch(batchId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/{batchId}/instructors")
    @ResponseBody
    public ResponseEntity<?> assignInstructor(@PathVariable("batchId") Long batchId, @RequestParam("instructorId") Long instructorId) {
        batchService.assignInstructor(batchId, instructorId);
        return ResponseEntity.ok().build();
    }
}