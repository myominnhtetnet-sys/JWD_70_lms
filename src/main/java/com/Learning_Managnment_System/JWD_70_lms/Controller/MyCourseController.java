package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.MyCourseService;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class MyCourseController {

    @Autowired
    private MyCourseService courseService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 🟢 1. SHOW STUDENT ENROLLED COURSES DASHBOARD
     * Target URL: http://localhost:8080/student/my-courses
     * Return view: my-courses.html
     */
    @GetMapping("/my-courses")
    public String showMyCoursesDashboard(Model model, HttpSession session) {
        // 1. Recover the secure email string token set directly by your LoginController
        String studentEmail = (String) session.getAttribute("email");
        Object roleId = session.getAttribute("roleId");
        
        // Safety verification check: If user session has expired or role isn't STUDENT (3), kick to login page
        if (studentEmail == null || roleId == null || (int) roleId != 3) {
            System.out.println("❌ SESSION SECURITY CHECK FAILED: Redirecting to login context.");
            return "redirect:/login";
        }
        
        System.out.println("Base URL Mapping initialized. Fetching enrolled courses for Student Email: " + studentEmail);
        
        // 2. Query the data layer using the active email variable string token
        List<Map<String, Object>> enrolledCourses = courseService.getEnrolledCoursesProgressByEmail(studentEmail);
        model.addAttribute("enrolledCourses", enrolledCourses);
        
        return "my-courses";
    }
    
    /**
     * 🟢 2. RENDER CLASSROOM MOODLE TIMELINE MODULE MAP
     * Target URL: http://localhost:8080/student/course/{id}
     * Return view: student-classroom.html
     */
    @GetMapping("/course/{id}")
    public String showClassroomDetail(@PathVariable("id") int courseId, Model model, HttpSession session) {
        // 1. Recover secure session email token
        String studentEmail = (String) session.getAttribute("email");
        Object roleId = session.getAttribute("roleId");
        
        if (studentEmail == null || roleId == null || (int) roleId != 3) {
            return "redirect:/login";
        }

        // 2. Fetch data via service layer routines
        MyCourseBean courseMeta = courseService.getCourseMetaDetails(courseId, studentEmail);
        
        // Safety check if student isn't enrolled or course doesn't exist
        if (courseMeta == null) {
            return "redirect:/student/my-courses";
        }
        
        // Reuse the query logic to fetch individual module lessons with their DONE status
        List<LessonProgressBean> lessonTimeline = 
                courseService.getCourseLessonTimeline(courseId, courseMeta.getBatchCode());

        // 3. Bind properties to the view layout model
        model.addAttribute("course", courseMeta);
        model.addAttribute("lessons", lessonTimeline);
        
        return "student-classroom";
    }

    /**
     * 🟢 3. RENDER SPLIT-SCREEN INTERACTIVE LESSON WORKSPACE LAYER
     * Target URL: http://localhost:8080/student/course/lesson/{id}?courseId=X
     * Return view: student-lesson-view.html
     */
    @GetMapping("/course/lesson/{id}")
    public String showLessonWorkspace(
            @PathVariable("id") int lessonId, 
            @RequestParam("courseId") int courseId, 
            Model model, 
            HttpSession session) {
            
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        // A. Fetch current active Lesson core details mapping parameters
        String currentLessonSql = "SELECT l.lesson_id AS lessonId, l.title AS lessonTitle, " +
                                  "l.content_type AS contentType, " +
                                  "m.title AS moduleTitle, l.duration_min AS durationMin " +
                                  "FROM lms_db.lessons l " +
                                  "JOIN lms_db.course_modules m ON l.module_id = m.module_id " +
                                  "WHERE l.lesson_id = ? LIMIT 1";
        
        Map<String, Object> activeLesson = jdbcTemplate.queryForMap(currentLessonSql, lessonId);

        // B. Conditional parameters safe extraction block
        try {
            String extraFieldsSql = "SELECT video_url, content FROM lms_db.lessons WHERE lesson_id = ?";
            Map<String, Object> extras = jdbcTemplate.queryForMap(extraFieldsSql, lessonId);
            activeLesson.put("videoUrl", extras.get("video_url"));
            activeLesson.put("content", extras.get("content"));
        } catch (Exception e) {
            activeLesson.put("videoUrl", null);
            activeLesson.put("content", "Text content body unassigned for this lesson chapter.");
        }

        // C. Check if THIS SPECIFIC LOGGED-IN STUDENT has completed this active lesson
        String currentLessonDoneCheck = "SELECT COUNT(*) FROM lms_db.class_sessions cs " +
                                        "JOIN lms_db.batches b ON cs.batch_id = b.batch_id " +
                                        "WHERE b.course_id = ? AND cs.lesson_id = ? " +
                                        "AND cs.title = CONCAT(?, ' [', ?, ']') AND cs.status = 'DONE'";
        
        Integer isCurrentLessonDone = jdbcTemplate.queryForObject(
            currentLessonDoneCheck, 
            Integer.class, 
            courseId, 
            lessonId, 
            activeLesson.get("lessonTitle"), 
            studentEmail
        );
        activeLesson.put("done", isCurrentLessonDone != null && isCurrentLessonDone > 0);

        // D. Fetch Course parent structural shell metadata configurations
        String courseSql = "SELECT c.course_id AS courseId, c.title AS courseTitle, b.batch_id AS batchId " +
                           "FROM lms_db.courses c " +
                           "JOIN lms_db.batches b ON b.course_id = c.course_id " +
                           "JOIN lms_db.enrollments en ON en.batch_id = b.batch_id " +
                           "JOIN lms_db.users u ON en.user_id = u.user_id " +
                           "WHERE u.email = ? AND c.course_id = ? AND en.status = 'ACTIVE' LIMIT 1";
                           
        Map<String, Object> courseData = jdbcTemplate.queryForMap(courseSql, studentEmail, courseId);

        // E. Query the list of syllabus timeline sidebar items isolated EXCLUSIVELY for this student's email context
        String checkmarksSql = "SELECT l.lesson_id AS lessonId, l.title AS lessonTitle, l.content_type AS contentType, l.duration_min AS durationMin, " +
                               "( " +
                               "  SELECT COUNT(*) FROM lms_db.class_sessions cs " +
                               "  WHERE cs.batch_id = b.batch_id AND cs.lesson_id = l.lesson_id " +
                               "  AND cs.title = CONCAT(l.title, ' [', ?, ']') AND cs.status = 'DONE' " +
                               ") > 0 AS done " +
                               "FROM lms_db.lessons l " +
                               "JOIN lms_db.course_modules m ON l.module_id = m.module_id " +
                               "JOIN lms_db.batches b ON b.course_id = m.course_id " +
                               "JOIN lms_db.enrollments en ON en.batch_id = b.batch_id " +
                               "JOIN lms_db.users su ON en.user_id = su.user_id " +
                               "WHERE su.email = ? AND m.course_id = ? AND en.status = 'ACTIVE' " +
                               "ORDER BY m.module_id ASC, l.lesson_id ASC";

        List<Map<String, Object>> sidebarLessons = jdbcTemplate.queryForList(checkmarksSql, studentEmail, studentEmail, courseId);

        // F. Bind datasets back to Thymeleaf layout engine
        model.addAttribute("lesson", activeLesson);
        model.addAttribute("course", courseData);
        model.addAttribute("sidebarLessons", sidebarLessons);

        return "student-lesson-view";
    }

    /**
     * 🟢 4. PROCESS STUDENT REQUEST TO MARK AN INDIVIDUAL LESSON AS COMPLETED
     * Target URL: http://localhost:8080/student/course/lesson/complete
     */
    @PostMapping("/course/lesson/complete")
    public String completeLessonAction(
            @RequestParam("lessonId") int lessonId, 
            @RequestParam("courseId") int courseId,
            HttpSession session) {
            
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        // Query the student's authentic active batch_id matching this course context exclusively
        String batchLookupSql = "SELECT e.batch_id FROM lms_db.enrollments e " +
                                "JOIN lms_db.users u ON e.user_id = u.user_id " +
                                "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                                "WHERE u.email = ? AND b.course_id = ? AND e.status = 'ACTIVE' LIMIT 1";
        
        int verifiedBatchId;
        try {
            verifiedBatchId = jdbcTemplate.queryForObject(batchLookupSql, Integer.class, studentEmail, courseId);
        } catch (Exception e) {
        	System.out.println("❌ ERROR: No active enrollment batch found for user: " + studentEmail + " in Course ID: " + courseId);
        	return "redirect:/student/my-courses";
        	}
        	// Pass the studentEmail directly to track progress on the fly with zero schema mutations
        	courseService.markLessonAsCompleted(verifiedBatchId, lessonId, studentEmail);
        	System.out.println("🔒 ISOLATED DONE: Lesson ID " + lessonId + " locked to student: " + studentEmail);
        	return "redirect:/student/course/lesson/" + lessonId + "?courseId=" + courseId;
        	}
        	/**
        	* 🟢 5. PROCESS STUDENT REQUEST TO UNDO/REGRESS THEIR LESSON PROGRESS
        	* Target URL: http://localhost:8080/student/course/lesson/regress
        	*/
        	@PostMapping("/course/lesson/regress")
        	public String regressLessonAction(
        	@RequestParam("lessonId") int lessonId,
        	@RequestParam("courseId") int courseId,
        	HttpSession session) {
        	String studentEmail = (String) session.getAttribute("email");
        	if (studentEmail == null) return "redirect:/login";
        	// Query the student's active batch_id matching this course
        	String batchLookupSql = "SELECT e.batch_id FROM lms_db.enrollments e " +
        	"JOIN lms_db.users u ON e.user_id = u.user_id " +
        	"JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
        	"WHERE u.email = ? AND b.course_id = ? AND e.status = 'ACTIVE' LIMIT 1";
        	int verifiedBatchId;
        	try {
        	verifiedBatchId = jdbcTemplate.queryForObject(batchLookupSql, Integer.class, studentEmail, courseId);
        	} catch (Exception e) {
        	return "redirect:/student/my-courses";
        	}
        	// Execute progress regression safely based on email patterns
        	courseService.regressLessonProgress(verifiedBatchId, lessonId, studentEmail);
        	return "redirect:/student/course/lesson/" + lessonId + "?courseId=" + courseId;
        	}
        	/**
        	* 🟢 6. PROCESS MANUAL COURSE GRADUATION CHECKOUT
        	* Target URL: http://localhost:8080/student/course/complete
        	*/
        	@PostMapping("/course/complete")
        	public String completeCourseManualAction(@RequestParam("courseId") int courseId, HttpSession session) {
        	String studentEmail = (String) session.getAttribute("email");
        	if (studentEmail == null) return "redirect:/login";
        	boolean success = courseService.finalizeCourseManualCheckout(studentEmail, courseId);
        	if (success) {
        	System.out.println("🎓 MANUAL GRADUATION: Course ID " + courseId + " finalized by user " + studentEmail);
        	return "redirect:/student/awards";
        	}
        	return "redirect:/student/my-courses";
        	}
}