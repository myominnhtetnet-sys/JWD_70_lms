package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.CertificateService;

@Controller
@RequestMapping("/admin/certificates")
public class CertificateController {
	
	@Autowired
    private CertificateService certificateService;
	
    @GetMapping
    public String showCertificatePage(Model model) {
        model.addAttribute("certificates", certificateService.getAllCertificates());
        model.addAttribute("batches", certificateService.getAllBatches()); 
        return "Certificate_Management";
    }

    @GetMapping("/view/{id}")
    public String viewCertificate(@PathVariable Long id, Model model) {
        model.addAttribute("cert", certificateService.getCertificateById(id));
        return "Certificate_View"; 
    }

    @PostMapping("/issue-batch")
    public String issueCertificatesByBatchForm(@RequestParam("batchId") Long batchId, RedirectAttributes redirectAttributes) {
        Integer issuedBy = 1; 
        String resultMessage = certificateService.issueCertificatesByBatch(batchId, issuedBy);
        
        if (resultMessage.startsWith("SUCCESS")) {
            redirectAttributes.addFlashAttribute("successMessage", resultMessage);
        } else if (resultMessage.startsWith("WARNING")) {
            redirectAttributes.addFlashAttribute("warningMessage", resultMessage);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", resultMessage);
        }

        return "redirect:/admin/certificates";
    }

    @PostMapping("/revoke/{id}")
    public String revokeCertificateForm(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        boolean success = certificateService.revokeCertificate(id, reason);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Certificate revoked successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to revoke certificate.");
        }
        return "redirect:/admin/certificates";
    }
}