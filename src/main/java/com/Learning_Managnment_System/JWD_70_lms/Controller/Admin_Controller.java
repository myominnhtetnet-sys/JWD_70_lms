package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.Service.DashboardService;
import com.Learning_Managnment_System.JWD_70_lms.Service.UserService;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin_Controller {

    @Autowired
    private Admin_Repository adminRepository;
    
    /**
     * Admin ဟုတ်မဟုတ် စစ်ဆေးသည့် Helper Method
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
        
        return roleId != 1; // 1 သည် Admin ဟု သတ်မှတ်သည်
    }

    /**
     * 🟢 @ModelAttribute ဖြင့် Controller ရှိ Endpoint အားလုံးအတွက် 
     * Login ဝင်ထားသော User ၏ အမည်နှင့် Role အမည်ကို Model သို့ အလိုအလျောက် ထည့်သွင်းပေးခြင်း
     */
    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        String displayName = "Administrator";
        int roleId = 1;

        // 1. Session ထဲမှ "currentUser" (LoginBean) ရှိမရှိ စစ်ဆေးခြင်း
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        if (currentUser != null) {
            if (currentUser.getFull_name() != null) {
                displayName = currentUser.getFull_name();
            }
            roleId = currentUser.getRole_id();
        } 
        // 2. သို့မဟုတ် သီးသန့် Session တင်ထားလျှင် ၎င်းတို့ကို ယူခြင်း
        else {
            if (session.getAttribute("fullName") != null) {
                displayName = (String) session.getAttribute("fullName");
            }
            if (session.getAttribute("roleId") != null) {
                roleId = (int) session.getAttribute("roleId");
            }
        }

        // Database မှ role_id ကို အခြေခံ၍ Role Name ဖော်ထုတ်ခြင်း
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

    @Autowired
    private UserService userService;

    @GetMapping({"", "/"})
    
    public String show(HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        return "redirect:/admin/dashboard";
    }

//    @GetMapping("/dashboard")
//    public String adminDashboard(HttpSession session, Model model) {
//        if (isNotAdmin(session)) {
//            return "redirect:/login";
//        }
//        List<AdminUserBean> users = adminRepository.getAllUsers();
//        model.addAttribute("users", users);
//        return "dashboard";
//    }


@Autowired
private DashboardService dashboardService;
@GetMapping("/dashboard")
public String showDashboard(Model model) {
    model.addAttribute("activePage", "dashboard");
    model.addAttribute("banner", dashboardService.getBannerSummary());
    model.addAttribute("stats", dashboardService.getStatCardsSummary());
    model.addAttribute("pendingPayments", dashboardService.getPendingPayments());
    model.addAttribute("batches", dashboardService.getBatchSeatProgress());
    model.addAttribute("overdues", dashboardService.getOverdueInstallmentsList());
    return "dashboard";
}
    
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        List<AdminUserBean> users = adminRepository.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/search")
    public String searchUsers(
            @RequestParam("keyword") String keyword,
            HttpSession session,
            Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        List<AdminUserBean> users = adminRepository.searchUsers(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(HttpSession session, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("user", new AdminUserBean());
        return "admin/user_form";
    }

    @PostMapping("/users/add")
    public String addUser(
            AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        if (user.getFull_name() == null || user.getFull_name().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Full name is required.");
            return "redirect:/admin/users";
        }

        if (user.getPassword_hash() == null || user.getPassword_hash().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password is required.");
            return "redirect:/admin/users";
        }

        if (adminRepository.emailExists(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "This email is already registered.");
            return "redirect:/admin/users";
        }

        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus("ACTIVE");
        }

        int result = adminRepository.addUser(user);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User added successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to add user.");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(
            @PathVariable("id") int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        AdminUserBean user = adminRepository.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/user_view";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(
            @PathVariable("id") int id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        AdminUserBean user = adminRepository.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/user_form";
    }

    @GetMapping("/users/edit-json/{id}")
    @ResponseBody
    public AdminUserBean getUserJson(@PathVariable("id") int id, HttpSession session) {
        if (isNotAdmin(session)) {
            return null;
        }
        return adminRepository.getUserById(id);
    }

    @PostMapping("/users/update")
    public String updateUser(
            AdminUserBean user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        if (user.getFull_name() == null || user.getFull_name().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Full name is required.");
            return "redirect:/admin/users";
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email is required.");
            return "redirect:/admin/users";
        }

        if (adminRepository.emailExistsForOtherUser(user.getEmail(), user.getUser_id())) {
            redirectAttributes.addFlashAttribute("error", "This email is already used by another user.");
            return "redirect:/admin/users";
        }

        int result = adminRepository.updateUser(user);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update user.");
        }

        return "redirect:/admin/users";
    }

    // 🟢 Suspend Method (Comment ဖြုတ်ပြီး ပြန်လည်ပြင်ဆင်ထားသည်)
    @PostMapping("/users/suspend")
    public String suspendUser(
            @RequestParam("userId") int userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        int result = adminRepository.updateStatus(userId, "SUSPENDED");

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User suspended successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to suspend user.");
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/activate")
    public String activateUser(
            @RequestParam("userId") int userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        int result = adminRepository.updateStatus(userId, "ACTIVE");

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User activated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to activate user.");
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(
            @RequestParam("userId") int userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) {
            return "redirect:/login";
        }

        int result = adminRepository.deleteUser(userId);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to delete user.");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/trash")
    public String viewTrashInMain(Model model) {
        List<AdminUserBean> deletedUsers = userService.getDeletedUsers();
        model.addAttribute("deletedUsers", deletedUsers);
        model.addAttribute("isTrashView", true); 
        return "users";
    }

    @PostMapping("/users/restore")
    public String restoreUser(@RequestParam("userId") int userId, RedirectAttributes redirectAttributes) {
        userService.restoreUser(userId);
        redirectAttributes.addFlashAttribute("success", "User restored successfully!");
        return "redirect:/admin/users";
    }
}