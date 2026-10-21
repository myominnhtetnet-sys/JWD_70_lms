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

	/* private final String UPLOAD_DIR = "uploads/materials/"; */

    private boolean isNotTeacher(HttpSession session) {
        Object roleId = session.getAttribute("roleId");
        return roleId == null || (int) roleId != 2;
    }

    private void addCommonAttributes(HttpSession session, Model model) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        int instructorId = currentUser != null ? currentUser.getUser_id() : 2;

        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("batches", teacherRepository.getBatchesByInstructor(instructorId));
        model.addAttribute("courses", teacherRepository.getCoursesByInstructor(instructorId));
    }

    // 1. Dashboard Route
    @GetMapping({"", "/", "/dashboard"})
    public String teacherDashboard(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        model.addAttribute("activeView", "dashboard");
        return "teacher"; 
    }

    // 2. Courses Route
    @GetMapping("/courses")
    public String myCourses(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
        model.addAttribute("activeView", "courses");
        return "teacher"; 
    }
    
	/*
	 * @GetMapping("/courses/{courseId}/builder") public String
	 * courseBuilder(@PathVariable("courseId") Long courseId, Model model) { // Make
	 * sure you pass a valid lessonId or list of lessons to the model!
	 * model.addAttribute("activeLessonId", 1L); // Example hardcoded or fetched ID
	 * for testing model.addAttribute("materials",
	 * teacherRepository.getMaterialsByLesson(1L)); return "course_builder"; }
	 */

    // 3. Batches Route
    @GetMapping("/batches")
    public String myBatches(HttpSession session, Model model) {
        if (isNotTeacher(session)) return "redirect:/login";
        addCommonAttributes(session, model);
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
        model.addAttribute("students", teacherRepository.getStudentsByBatch(id));
        model.addAttribute("activeView", "batch-detail"); 
        
        return "teacher";
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
                // 1. Fetch roster of students for the batch
                List<Map<String, Object>> students = teacherRepository.getRosterByBatch(batchId);
                model.addAttribute("students", students);
                
                // 2. Fetch saved attendance records for this session
                List<Map<String, Object>> savedRows = teacherRepository.getAttendanceBySession(sessionId);
                
                // 3. Map enrollmentId -> status & enrollmentId -> marked_at timestamp for HTML lookup
                Map<String, Object> attendanceMap = new HashMap<>();
                Map<String, Object> markedAtMap = new HashMap<>();
                
                for (Map<String, Object> row : savedRows) {
                    // Convert enrollment_id to String/Long safely depending on your DB driver
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
        Long batchId = attendanceForm.getBatchId(); // <-- Grab batchId
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
        
        // Redirect back carrying BOTH batchId and sessionId so the view stays fully intact
        return "redirect:/teacher/attendance?batchId=" + batchId + "&sessionId=" + sessionId;
    }
	}