//package com.Learning_Managnment_System.JWD_70_lms.Controller;
//
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.Learning_Managnment_System.JWD_70_lms.Service.FinalGradeService;
//import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
//
//@Controller
//@RequestMapping("/teacher/final-grades")
//public class TeacherFinalGradeController {
//
//    private final FinalGradeService service;
//
//    public TeacherFinalGradeController(FinalGradeService service) {
//        this.service = service;
//    }
//
//    @GetMapping
//    public String list(
//            @RequestParam(required = false) Integer batchId,
//            HttpSession session,
//            Model model) {
//
//        LoginBean teacher = teacher(session);
//
//        if (teacher == null) {
//            return "redirect:/login";
//        }
//
////        model.addAttribute(
////                "batches", service.getBatches(teacher.getUser_id()));
//        int teacherId = teacher.getUser_id();
//
//        List<Map<String, Object>> batches =
//                service.getBatches(teacherId);
//
//        System.out.println("Final Grade Teacher ID = " + teacherId);
//        System.out.println("Final Grade Teacher Email = " + teacher.getEmail());
//        System.out.println("Final Grade Batch Count = " + batches.size());
//
//        model.addAttribute("batches", batches);
//
//        model.addAttribute("selectedBatchId", batchId);
//        model.addAttribute("students", List.of());
//        model.addAttribute("canPublish", false);
//
//        if (batchId != null) {
//            try {
//                List<Map<String, Object>> students =
//                        service.getStudents(batchId, teacher.getUser_id());
//
//                model.addAttribute("students", students);
//
//                boolean hasDraft = students.stream().anyMatch(
//                        row -> row.get("publishedAt") == null);
//
//                boolean allCalculated = !students.isEmpty()
//                        && students.stream().allMatch(
//                                row -> row.get("gradeId") != null);
//
//                model.addAttribute(	
//                        "canPublish", hasDraft && allCalculated);
//
//            } catch (IllegalArgumentException ex) {
//                model.addAttribute("errorMessage", ex.getMessage());
//                model.addAttribute("selectedBatchId", null);
//            }
//        }
//
//        return "teacher-final-grade";
//    }
//
//    @PostMapping("/calculate")
//    public String calculate(
//            @RequestParam int batchId,
//            HttpSession session,
//            RedirectAttributes redirect) {
//
//        LoginBean teacher = teacher(session);
//
//        if (teacher == null) {
//            return "redirect:/login";
//        }
//
//        try {
//            int count = service.calculate(batchId, teacher.getUser_id());
//
//            redirect.addFlashAttribute(
//                    "successMessage",
//                    "Calculated " + count
//                    + " draft grades. Review before publishing.");
//
//        } catch (IllegalArgumentException ex) {
//            redirect.addFlashAttribute(
//                    "errorMessage", ex.getMessage());
//        }
//
//        redirect.addAttribute("batchId", batchId);
//        return "redirect:/teacher/final-grades";
//    }
//
//    @PostMapping("/publish")
//    public String publish(
//            @RequestParam int batchId,
//            HttpSession session,
//            RedirectAttributes redirect) {
//
//        LoginBean teacher = teacher(session);
//
//        if (teacher == null) {
//            return "redirect:/login";
//        }
//
//        try {
//            int count = service.publish(batchId, teacher.getUser_id());
//
//            redirect.addFlashAttribute(
//                    "successMessage",
//                    "Published " + count + " final grades.");
//
//        } catch (IllegalArgumentException ex) {
//            redirect.addFlashAttribute(
//                    "errorMessage", ex.getMessage());
//        }
//
//        redirect.addAttribute("batchId", batchId);
//        return "redirect:/teacher/final-grades";
//    }
//
//    private LoginBean teacher(HttpSession session) {
//        Object value = session.getAttribute("currentUser");
//
//        if (value instanceof LoginBean user
//                && user.getRole_id() == 2
//                && user.getUser_id() > 0) {
//            return user;
//        }
//
//        return null;
//    }
//}
