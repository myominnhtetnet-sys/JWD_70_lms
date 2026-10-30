package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Repository.LoginRepository;
import com.Learning_Managnment_System.JWD_70_lms.Service.AnnouncementService;
import com.Learning_Managnment_System.JWD_70_lms.Service.StudentDashboardService;
import com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	 @Autowired
	    private AnnouncementService announcementService;

    @Autowired
    private LoginRepository loginRepo;
    
    @Autowired
    StudentDashboardService dashboardService;

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("login", new LoginBean());
        return "login";
    }
    
//    @PostMapping("/login")
//    public String login(@ModelAttribute("login") LoginBean login, HttpSession session,
//            RedirectAttributes redirectAttributes) {
//        LoginBean user = loginRepo.login(login.getEmail(), login.getPassword_hash());
//        if (user == null) {
//            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
//            return "redirect:/login";
//        }
//        
//        // 🟢 ADD THIS LINE: Save the authenticated user record to match your view checks
//        session.setAttribute("currentUser", user);
//        
//        // Keep your existing simple attribute mappings
//        session.setAttribute("roleId", user.getRole_id());
//        session.setAttribute("fullName", user.getFull_name());
//        session.setAttribute("email", user.getEmail());
//        
//      
//        session.setAttribute("studentId", user.getRole_id()); 
//
//        if (user.getRole_id() == 1) {
//            return "redirect:/admin";
//        } else if (user.getRole_id() == 2) {
//            return "redirect:/teacher";
//        } else if (user.getRole_id() == 3) {
//            return "redirect:/student";
//        }
//
//        return "redirect:/login";
//    }
    @PostMapping("/login")
    public String login(@ModelAttribute("login") LoginBean login, HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        LoginBean user = loginRepo.login(login.getEmail(), login.getPassword_hash());
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }
        
        // 🟢 အကောင့် Suspend ဖြစ်နေခြင်း ရှိမရှိ စစ်ဆေးခြင်း
        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Your account has been suspended");
            return "redirect:/login";
        }

        // ကျန်ရှိသော Session များနှင့် Role အလိုက် လမ်းကြောင်းပြောင်းခြင်း...
        session.setAttribute("currentUser", user);
        session.setAttribute("roleId", user.getRole_id());
        session.setAttribute("fullName", user.getFull_name());
        session.setAttribute("email", user.getEmail());

        if (user.getRole_id() == 1) {
            return "redirect:/admin";
        } else if (user.getRole_id() == 2) {
            return "redirect:/teacher";
        } else if (user.getRole_id() == 3) {
            return "redirect:/student";
        }

        return "redirect:/login";
    }
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate; // 👈 1. Ensure JdbcTemplate is autowired at the top of LoginController
//
//    @GetMapping("/student")
//    public String studentPage(HttpSession session, Model model) {
//        // Enforce Session Security Guard Boundaries
//        if (session.getAttribute("roleId") == null || (int) session.getAttribute("roleId") != 3) {
//            return "redirect:/login";
//        }
//
//        String email = (String) session.getAttribute("email");
//        
//        // Fetch aggregated transactional model map metrics out of business service
//        Map<String, Object> dashboardData = dashboardService.getStudentDashboardData(email);
//        
//        // Populate Thymeleaf UI scope variables natively
//        model.addAllAttributes(dashboardData);
//        
//        // Active link tracking token state for sidebar component highlighting
//        model.addAttribute("activeLink", "dashboard");
//
//        return "studentdashboard";
//    }
    @GetMapping("/student")
    public String studentPage(HttpSession session, Model model) {
        if (session.getAttribute("roleId") == null || (int) session.getAttribute("roleId") != 3) {
            return "redirect:/login";
        }

        String email = (String) session.getAttribute("email");
        
        // 🟢 Database ထဲမှ User ၏ လက်ရှိ Status ကို စစ်ဆေးခြင်း
        LoginBean freshUser = loginRepo.findByEmail(email); 
        if (freshUser == null || "SUSPENDED".equalsIgnoreCase(freshUser.getStatus())) {
            session.invalidate(); // Session ကို ဖျက်မည်
            return "redirect:/login?suspended=true";
        }

        Map<String, Object> dashboardData = dashboardService.getStudentDashboardData(email);
        model.addAllAttributes(dashboardData);
     
        
        // 1. Fetch only the 2 latest items for the quick dashboard card feed view
        List<AnnouncementBean> recentAnnouncements = announcementService.getLatestAnnouncements(2);
        model.addAttribute("allAnnouncements", recentAnnouncements);
        
        // 2. Fetch the entire collection list to hydrate the history modal container frame
        List<AnnouncementBean> fullHistoryList = announcementService.getAllAnnouncements();
        model.addAttribute("historyAnnouncements", fullHistoryList);
        
        model.addAttribute("activeLink", "dashboard");
        model.addAttribute("fullName", session.getAttribute("fullName")); 
        return "studentdashboard";
    }
    



    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object roleId = session.getAttribute("roleId");

        if (roleId == null) {
            return "redirect:/login";
        }

        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("roleId", session.getAttribute("roleId"));

        return "dashboard";
    }
}