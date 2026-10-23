package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Learning_Managnment_System.JWD_70_lms.Service.CertificateService;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;

@Controller
@RequestMapping("/admin/certificates")
public class CertificateController {
	
	@Autowired
    private CertificateService certificateService;
	
    // 1. Certificate Dashboard Page Display
    @GetMapping
    public String showCertificatePage(Model model) {
        List<CertificateBean> certificates = certificateService.getAllCertificates();
        model.addAttribute("certificates", certificates);
        
        // 🟢 ကျောင်းသားများ၏ Course နှင့် Batch ဆိုင်ရာ Enrollment စာရင်းများကို Modal Dropdown အတွက် ပို့ပေးခြင်း
        model.addAttribute("enrollments", certificateService.getAllEligibleEnrollmentsForCertificate());
        
        return "Certificate_Management"; // src/main/resources/templates/Certificate_Management.html
    }

    // 2. Dynamic Certificate View & Download Page (PNG / PDF)
    @GetMapping("/view/{id}")
    public String viewCertificate(@PathVariable Long id, Model model) {
        CertificateBean cert = certificateService.getCertificateById(id);
        model.addAttribute("cert", cert);
        return "Certificate_View"; // src/main/resources/templates/Certificate_View.html
    }

    // 3. Form Submit - Issue Certificate
    @PostMapping("/issue")
    public String issueCertificateForm(@ModelAttribute CertificateBean cert) {
        certificateService.issueCertificate(cert);
        return "redirect:/admin/certificates";
    }

    // 4. Form Submit - Revoke Certificate
    @PostMapping("/revoke/{id}")
    public String revokeCertificateForm(@PathVariable Long id, @RequestParam String reason) {
        certificateService.revokeCertificate(id, reason);
        return "redirect:/admin/certificates";
    }

    // ==================== REST APIs ====================

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<CertificateBean>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CertificateBean> getCertificateById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @PostMapping("/api/issue")
    @ResponseBody
    public ResponseEntity<String> issueCertificateApi(@RequestBody CertificateBean cert) {
        boolean success = certificateService.issueCertificate(cert);
        if (success) {
            return ResponseEntity.ok("Certificate issued successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to issue certificate.");
    }

    @PostMapping("/api/revoke/{id}")
    @ResponseBody
    public ResponseEntity<String> revokeCertificateApi(@PathVariable Long id, @RequestParam String reason) {
        boolean success = certificateService.revokeCertificate(id, reason);
        if (success) {
            return ResponseEntity.ok("Certificate revoked successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to revoke certificate.");
    }
}