package com.Learning_Managnment_System.JWD_70_lms.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.CertificateService;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

@Controller
@RequestMapping("/admin/certificates")
public class CertificateController {
	
	@Autowired
    private CertificateService certificateService;
	
    /**
     * 🛡️ Admin ဟုတ်မဟုတ် စစ်ဆေးသည့် Helper Method
     */
    private boolean isNotAdmin(HttpSession session) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        Object roleIdObj = session.getAttribute("roleId");
        
        int roleId = 0;
        if (currentUser != null) {
            roleId = currentUser.getRole_id();
        } else if (roleIdObj != null) {
            roleId = (int) roleIdObj;
        }
        
        return roleId != 1; // 1 သည် Administrator ဟု သတ်မှတ်သည်
    }

    /**
     * 🟢 @ModelAttribute ဖြင့် Controller ရှိ Endpoint အားလုံးအတွက် 
     * Login ဝင်ထားသော User ၏ အမည်နှင့် Role အမည်ကို Model သို့ အလိုအလျောက် ထည့်သွင်းပေးခြင်း
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

        String roleName = "User";
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
    public String showCertificatePage(HttpSession session, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("certificates", certificateService.getAllCertificates());
        model.addAttribute("batches", certificateService.getAllBatches()); 
        return "Certificate_Management";
    }

    @GetMapping("/view/{id}")
    public String viewCertificate(HttpSession session, @PathVariable Long id, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("cert", certificateService.getCertificateById(id));
        return "Certificate_View"; 
    }

    @PostMapping("/issue-batch")
    public String issueCertificatesByBatchForm(HttpSession session, @RequestParam("batchId") Long batchId, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        // 🟢 Session ထဲမှ Login ဝင်ထားသူ၏ ID ကို ယူ၍ issuedBy အဖြစ် အသုံးပြုခြင်း (မရှိလျှင် default 1)
        Integer issuedBy = 1;
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            issuedBy = currentUser.getRole_id(); // သို့မဟုတ် currentUser.getUser_id() (LoginBean တွင်ပါရှိသည့် field အပေါ်မူတည်သည်)
        } else if (session.getAttribute("userId") != null) {
            Object uIdObj = session.getAttribute("userId");
            if (uIdObj instanceof Integer) {
                issuedBy = (Integer) uIdObj;
            } else if (uIdObj instanceof Long) {
                issuedBy = ((Long) uIdObj).intValue();
            }
        }

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
    public String revokeCertificateForm(HttpSession session, @PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = certificateService.revokeCertificate(id, reason);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Certificate revoked successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to revoke certificate.");
        }
        return "redirect:/admin/certificates";
    }
}