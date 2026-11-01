/*
 * package com.Learning_Managnment_System.JWD_70_lms.Controller;
 * 
 * import
 * com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRepository;
 * // Import your discount type repository or service here (adjust package name
 * if needed) // import
 * com.Learning_Managnment_System.JWD_70_lms.Repository.DiscountTypeRepository;
 * import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean; import
 * com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;
 * 
 * import jakarta.servlet.http.HttpSession;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.dao.DataIntegrityViolationException; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.*; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import jakarta.servlet.http.HttpSession; import java.util.List;
 * 
 * @Controller
 * 
 * @RequestMapping("/admin/scholarships") public class
 * AdminScholarshipController {
 * 
 * @Autowired private ScholarshipRepository scholarshipRepository; private
 * boolean isNotAdmin(HttpSession session) { LoginBean currentUser = (LoginBean)
 * session.getAttribute("currentUser"); Object roleIdObj =
 * session.getAttribute("roleId");
 * 
 * int roleId = 0; if (currentUser != null) { roleId = currentUser.getRole_id();
 * } else if (roleIdObj != null) { roleId = (int) roleIdObj; }
 * 
 * return roleId != 1; }
 * 
 * @ModelAttribute public void addCommonAttributes(HttpSession session, Model
 * model) { String displayName = "Administrator"; int roleId = 1;
 * 
 * LoginBean currentUser = (LoginBean) session.getAttribute("currentUser"); if
 * (currentUser != null) { if (currentUser.getFull_name() != null) { displayName
 * = currentUser.getFull_name(); } roleId = currentUser.getRole_id(); }
 * 
 * else { if (session.getAttribute("fullName") != null) { displayName = (String)
 * session.getAttribute("fullName"); } if (session.getAttribute("roleId") !=
 * null) { roleId = (int) session.getAttribute("roleId"); } }
 * 
 * // Database မှ role_id ကို အခြေခံ၍ Role Name ဖော်ထုတ်ခြင်း String roleName =
 * "User"; if (roleId == 1) { roleName = "Administrator"; } else if (roleId ==
 * 2) { roleName = "Instructor"; } else if (roleId == 3) { roleName = "Student";
 * }
 * 
 * model.addAttribute("fullName", displayName);
 * model.addAttribute("loggedInRole", roleName); }
 * 
 * 
 * private boolean isNotAdmin(HttpSession session) { LoginBean currentUser =
 * (LoginBean) session.getAttribute("currentUser"); Object roleIdObj =
 * session.getAttribute("roleId");
 * 
 * int roleId = 0; if (currentUser != null) { roleId = currentUser.getRole_id();
 * } else if (roleIdObj != null) { roleId = (int) roleIdObj; }
 * 
 * return roleId != 1; }
 * 
 * @ModelAttribute public void addCommonAttributes(HttpSession session, Model
 * model) { String displayName = "Administrator"; int roleId = 1;
 * 
 * LoginBean currentUser = (LoginBean) session.getAttribute("currentUser"); if
 * (currentUser != null) { if (currentUser.getFull_name() != null) { displayName
 * = currentUser.getFull_name(); } roleId = currentUser.getRole_id(); } else {
 * if (session.getAttribute("fullName") != null) { displayName = (String)
 * session.getAttribute("fullName"); } if (session.getAttribute("roleId") !=
 * null) { roleId = (int) session.getAttribute("roleId"); } }
 * 
 * String roleName = "Administrator"; if (roleId == 1) { roleName =
 * "Administrator"; } else if (roleId == 2) { roleName = "Instructor"; } else if
 * (roleId == 3) { roleName = "Student"; }
 * 
 * model.addAttribute("fullName", displayName);
 * model.addAttribute("loggedInRole", roleName); }
 * 
 * @GetMapping({"", "/", "/list"}) public String listScholarships(HttpSession
 * session, Model model) { if (isNotAdmin(session)) { return "redirect:/login";
 * } List<ScholarshipBean> scholarships =
 * scholarshipRepository.getAllScholarships();
 * model.addAttribute("scholarships", scholarships); return "Scholarship-list";
 * }
 * 
 * @GetMapping("/new") public String showAddForm(HttpSession session, Model
 * model) { if (isNotAdmin(session)) { return "redirect:/login"; }
 * ScholarshipBean scholarship = new ScholarshipBean();
 * scholarship.setIsActive(true); // Default active status
 * model.addAttribute("scholarship", scholarship);
 * 
 * return "Scholarship-form"; }
 * 
 * // ========================================= // SAVE / UPDATE SCHOLARSHIP //
 * =========================================
 * 
 * @PostMapping("/save") public String saveScholarship(
 * 
 * @ModelAttribute("scholarship") ScholarshipBean scholarship, HttpSession
 * session, RedirectAttributes redirectAttributes, Model model) {
 * 
 * if (isNotAdmin(session)) { return "redirect:/login"; }
 * 
 * try { boolean isNew = scholarship.getScholarshipId() == null ||
 * scholarship.getScholarshipId() <= 0;
 * 
 * if (isNew) { scholarshipRepository.createScholarship(scholarship);
 * redirectAttributes.addFlashAttribute("successMessage",
 * "Scholarship created successfully."); } else {
 * scholarshipRepository.updateScholarship(scholarship.getScholarshipId(),
 * scholarship); redirectAttributes.addFlashAttribute("successMessage",
 * "Scholarship updated successfully."); }
 * 
 * return "redirect:/admin/scholarships/list";
 * 
 * } catch (DataIntegrityViolationException e) { if (e.getMessage() != null &&
 * e.getMessage().contains("fk_scholarships_type")) {
 * model.addAttribute("errorMessage",
 * "Selected discount type does not exist. Please choose a valid type."); } else
 * { model.addAttribute("errorMessage",
 * "A database error occurred. The scholarship name or details may already exist."
 * ); }
 * 
 * return "Scholarship-form"; } }
 * 
 * // ========================================= // EDIT SCHOLARSHIP FORM //
 * =========================================
 * 
 * @GetMapping("/edit/{id}") public String showEditForm(
 * 
 * @PathVariable("id") Integer id, HttpSession session, Model model) {
 * 
 * if (isNotAdmin(session)) { return "redirect:/login"; }
 * 
 * ScholarshipBean scholarship = scholarshipRepository.getScholarshipById(id);
 * 
 * if (scholarship == null) { return "redirect:/admin/scholarships/list"; }
 * 
 * model.addAttribute("scholarship", scholarship);
 * 
 * return "Scholarship-form"; }
 * 
 * // ========================================= // DELETE SCHOLARSHIP //
 * =========================================
 * 
 * @GetMapping("/delete/{id}") public String deleteScholarship(
 * 
 * @PathVariable("id") Integer id, HttpSession session, RedirectAttributes
 * redirectAttributes) {
 * 
 * if (isNotAdmin(session)) { return "redirect:/login"; }
 * 
 * try { scholarshipRepository.deleteScholarship(id);
 * redirectAttributes.addFlashAttribute("successMessage",
 * "Scholarship deleted successfully."); } catch
 * (DataIntegrityViolationException e) {
 * redirectAttributes.addFlashAttribute("errorMessage",
 * "Cannot delete this scholarship because student applications are already linked to it."
 * ); }
 * 
 * return "redirect:/admin/scholarships/list"; } }
 */