package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Learning_Managnment_System.JWD_70_lms.Service.BatchService;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.*;

@Controller
@RequestMapping("/admin/batches-manage")
public class BatchController {
	
	private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
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