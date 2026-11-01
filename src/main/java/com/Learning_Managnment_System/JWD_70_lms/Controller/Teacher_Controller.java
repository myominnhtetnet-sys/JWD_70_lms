package com.Learning_Managnment_System.JWD_70_lms.Controller;

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

import com.Learning_Managnment_System.JWD_70_lms.Repository.Teacher_Repository;
import com.Learning_Managnment_System.JWD_70_lms.Service.QuestionService;
import com.Learning_Managnment_System.JWD_70_lms.model.AttendanceBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class Teacher_Controller {

    @Autowired
    private Teacher_Repository teacherRepository;
    @Autowired
    private QuestionService questionService;

    private boolean isNotTeacher(HttpSession session) {
        Object roleId = session.getAttribute("roleId");
        return roleId == null || (int) roleId != 2;
    }

    private void addCommonAttributes(HttpSession session, Model model) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("batches", teacherRepository.getBatchesByInstructor(instructorId));
        
        List<Map<String, Object>> coursesWithStats = teacherRepository.getCoursesWithStatsByInstructor(instructorId);
        model.addAttribute("courses", coursesWithStats);

        // Compute counts globally so they are never null on any view
        long totalCount = coursesWithStats.size();
        long publishedCount = coursesWithStats.stream().filter(c -> "PUBLISHED".equals(c.get("status"))).count();
        long draftCount = coursesWithStats.stream().filter(c -> "DRAFT".equals(c.get("status"))).count();

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
    }

    // 1. Dashboard Route
    @GetMapping({"", "/", "/dashboard"})
    public String teacherDashboard(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        // Fetch and pass the 3 most recent questions to the model
        model.addAttribute("recentQuestions", questionService.getRecentQuestions(3));
        
        model.addAttribute("activeView", "dashboard");
        return "teacher"; 
    }

 // 2. Courses Route with explicit count variables matching Batches
    @GetMapping("/courses")
    public String myCourses(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;
        
        List <Map<String, Object>> coursesWithStats = teacherRepository.getCoursesWithStatsByInstructor(instructorId);
        
        long totalCount = coursesWithStats.size();
        long publishedCount = coursesWithStats.stream().filter(c -> "PUBLISHED".equals(c.get("status"))).count();
        long draftCount = coursesWithStats.stream().filter(c -> "DRAFT".equals(c.get("status"))).count();
        
        model.addAttribute("courses", coursesWithStats);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        
        model.addAttribute("activeView", "courses");
        return "teacher"; 
    }

 // 3. Batches Route with Course Filter support
    @GetMapping("/batches")
    public String myBatches(@RequestParam(value = "courseId", required = false) Long courseId, HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;
        
        List <Map<String, Object>> batchesList = teacherRepository.getBatchesByInstructor(instructorId);
        List <Map<String, Object>> coursesList = teacherRepository.getCoursesWithStatsByInstructor(instructorId);
        
        // Filter batches by courseId if selected
        if (courseId != null) {
            batchesList = batchesList.stream()
                .filter(b -> b.get("course_id") != null && Long.valueOf(b.get("course_id").toString()).equals(courseId))
                .toList();
        }
        
        long totalBatchCount = batchesList.size();
        long ongoingCount = batchesList.stream().filter(b -> "ONGOING".equals(b.get("status")) || "ACTIVE".equals(b.get("status"))).count();
        long upcomingCount = batchesList.stream().filter(b -> "UPCOMING".equals(b.get("status")) || "SCHEDULED".equals(b.get("status"))).count();
        long completedCount = batchesList.stream().filter(b -> "COMPLETED".equals(b.get("status")) || "FINISHED".equals(b.get("status"))).count();
        
        model.addAttribute("batches", batchesList);
        model.addAttribute("coursesList", coursesList);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("totalBatchCount", totalBatchCount);
        model.addAttribute("ongoingCount", ongoingCount);
        model.addAttribute("upcomingCount", upcomingCount);
        model.addAttribute("completedCount", completedCount);
        
        model.addAttribute("activeView", "batches");
        return "teacher";
    }

    // 3.1 Batch Detail Route
    @GetMapping("/batches/{id}")
    public String batchDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        model.addAttribute("batchId", id);
        model.addAttribute("batch", teacherRepository.getBatchById(id));
        model.addAttribute("students", teacherRepository.getBatchAttendanceSummary(id));
        model.addAttribute("activeView", "batch-detail"); 
        
        return "teacher";
    }

    // 3.2 Student Progress View Route
    @GetMapping("/batches/{batchId}/student/{enrollmentId}")
    public String studentProgressView(@PathVariable("batchId") Long batchId,
                                      @PathVariable("enrollmentId") Long enrollmentId,
                                      HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);

        Map<String, Object> studentInfo = teacherRepository.getStudentEnrollmentDetails(enrollmentId);
        List<Map<String, Object>> sessionAttendance = teacherRepository.getStudentSessionAttendanceList(batchId, enrollmentId);

        model.addAttribute("student", studentInfo);
        model.addAttribute("sessionList", sessionAttendance);
        model.addAttribute("batchId", batchId);

        model.addAttribute("activeView", "batch-detail");
        return "student_progress"; 
    }
    
    @GetMapping("/attendance")
    public String takeAttendance(@RequestParam(value = "batchId", required = false) Long batchId, 
                                 @RequestParam(value = "sessionId", required = false) Long sessionId,
                                 HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("selectedSessionId", sessionId);

        if (batchId != null) {
            model.addAttribute("sessions", teacherRepository.getSessionsByBatch(batchId));
            
            if (sessionId != null) {
                List<Map<String, Object>> students = teacherRepository.getRosterByBatch(batchId);
                model.addAttribute("students", students);
                
                List<Map<String, Object>> savedRows = teacherRepository.getAttendanceBySession(sessionId);
                
                Map<String, Object> attendanceMap = new HashMap<>();
                Map<String, Object> markedAtMap = new HashMap<>();
                
                for (Map<String, Object> row : savedRows) {
                    Object enrollmentIdObj = row.get("enrollment_id");
                    if (enrollmentIdObj != null) {
                        String enrollmentIdStr = enrollmentIdObj.toString();
                        attendanceMap.put(enrollmentIdStr, row.get("status"));
                        
                        Object markedAt = row.get("marked_at");
                        if (markedAt != null) {
                            markedAtMap.put(enrollmentIdStr, markedAt.toString());
                        }
                    }
                }
                
                model.addAttribute("attendanceMap", attendanceMap);
                model.addAttribute("markedAtMap", markedAtMap);
                
            } else {
                model.addAttribute("students", Collections.emptyList());
                model.addAttribute("attendanceMap", Collections.emptyMap());
                model.addAttribute("markedAtMap", Collections.emptyMap());
            }
        } else {
            model.addAttribute("sessions", Collections.emptyList());
            model.addAttribute("students", Collections.emptyList());
            model.addAttribute("attendanceMap", Collections.emptyMap());
            model.addAttribute("markedAtMap", Collections.emptyMap());
        }

        model.addAttribute("activeView", "attendance");
        return "teacher"; 
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@ModelAttribute AttendanceBean attendanceForm, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTeacher(session)) return "redirect:/login";
        
        Long sessionId = attendanceForm.getSessionId();
        Long batchId = attendanceForm.getBatchId(); 
        List<AttendanceBean.AttendanceItem> items = attendanceForm.getAttendanceList();
        
        Integer teacherId = (Integer) session.getAttribute("user_id");
        int markedBy = (teacherId != null) ? teacherId : 1; 

        if (sessionId != null && items != null) {
            for (AttendanceBean.AttendanceItem item : items) {
                if (item.getEnrollmentId() != null && item.getStatus() != null && !item.getStatus().trim().isEmpty()) {
                    teacherRepository.saveAttendance(sessionId, item.getEnrollmentId(), item.getStatus(), markedBy);
                }
            }
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Attendance saved successfully!");
        return "redirect:/teacher/attendance?batchId=" + batchId + "&sessionId=" + sessionId;
    }

    @PostMapping("/batches/student/save-progress")
    public String saveStudentProgress(@RequestParam("enrollmentId") Long enrollmentId,
                                      @RequestParam("batchId") Long batchId,
                                      @RequestParam(value = "progressStatus", required = false) String progressStatus,
                                      @RequestParam(value = "teacherNote", required = false) String teacherNote,
                                      HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTeacher(session)) return "redirect:/login";

        if (progressStatus != null) {
            teacherRepository.updateStudentProgressAndNote(enrollmentId, progressStatus, teacherNote);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Student progress updated successfully!");
        return "redirect:/teacher/batches/" + batchId;
    }
 // 5.1 Teacher Profile View
    @GetMapping("/profile")
    public String teacherProfile(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        Map<String, Object> profile = teacherRepository.getTeacherById(instructorId);
        if (profile != null) {
            model.addAttribute("fullName", profile.get("full_name"));
            model.addAttribute("email", profile.get("email"));
            model.addAttribute("phone", profile.get("phone"));
            model.addAttribute("address", profile.get("address"));
        }
        model.addAttribute("teacherId", instructorId);
        model.addAttribute("activeView", "profile");

        return "teacher_profile";
    }

    // 5.2 Update Personal Info Submission
    @PostMapping("/profile/update")
    public String updateTeacherProfile(@RequestParam("fullName") String fullName,
                                     @RequestParam(value = "phone", required = false) String phone,
                                     @RequestParam(value = "address", required = false) String address,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTeacher(session)) return "redirect:/login";

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        teacherRepository.updateTeacherProfile(instructorId, fullName, phone, address);
        
        // Update session name if changed
        session.setAttribute("fullName", fullName);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/teacher/profile";
    }

    // 5.3 Change Password Submission
    @PostMapping("/profile/change-password")
    public String changeTeacherPassword(@RequestParam("currentPassword") String currentPassword,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTeacher(session)) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
            return "redirect:/teacher/profile";
        }

        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        Map<String, Object> profile = teacherRepository.getTeacherById(instructorId);
        String dbPassword = profile != null ? (String) profile.get("password") : "";

        if (dbPassword != null && dbPassword.equals(currentPassword)) {
            teacherRepository.updateTeacherPassword(instructorId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
        }

        return "redirect:/teacher/profile";
    }	
}