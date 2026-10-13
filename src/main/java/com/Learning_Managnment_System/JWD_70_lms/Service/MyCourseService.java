package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MyCourseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 🟢 1. FETCH MAIN ENROLLED COURSES PROFILE PROGRESSION DATA
     * Subquery calculates totals isolated EXCLUSIVELY for this student's email signature token.
     */
    public List<Map<String, Object>> getEnrolledCoursesProgressByEmail(String email) {
        String sql = "SELECT c.course_id, b.batch_id, c.title AS course_title, b.batch_code, b.status AS batch_status, " +
                     "COALESCE(u.full_name, 'Staff Instructor') AS teacher_name, " +
                     "( " +
                     "  SELECT COUNT(DISTINCT cs.lesson_id) " +
                     "  FROM lms_db.class_sessions cs " +
                     "  WHERE cs.batch_id = b.batch_id " +
                     "  AND cs.status = 'DONE' " +
                     "  AND cs.title LIKE CONCAT('% [', ?, ']') " + // Only count this user's checkmarks!
                     "  AND cs.lesson_id IS NOT NULL " +
                     ") AS completed_lessons, " +
                     "( " +
                     "  SELECT COUNT(DISTINCT l.lesson_id) " +
                     "  FROM lms_db.course_modules m " +
                     "  JOIN lms_db.lessons l ON l.module_id = m.module_id " +
                     "  WHERE m.course_id = c.course_id AND l.status = 'PUBLISHED' " +
                     ") AS total_lessons " +
                     "FROM lms_db.enrollments e " +
                     "JOIN lms_db.users su ON e.user_id = su.user_id " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN lms_db.courses c ON b.course_id = c.course_id " +
                     "LEFT JOIN lms_db.course_instructors ci ON ci.course_id = c.course_id AND ci.is_primary = 1 " +
                     "LEFT JOIN lms_db.users u ON ci.instructor_id = u.user_id " +
                     "WHERE su.email = ? AND e.status = 'ACTIVE'";

        return jdbcTemplate.queryForList(sql, email, email);
    }

    /**
     * 🟢 2. FETCH SPECIFIC CLASSROOM METADATA HEADER DETAILS
     */
    public MyCourseBean getCourseMetaDetails(int courseId, String email) {
        String sql = "SELECT c.course_id AS courseId, c.title AS courseTitle, b.batch_code AS batchCode, b.status AS batchStatus " +
                     "FROM lms_db.courses c " +
                     "JOIN lms_db.batches b ON b.course_id = c.course_id " +
                     "JOIN lms_db.enrollments en ON en.batch_id = b.batch_id " +
                     "JOIN lms_db.users u ON en.user_id = u.user_id " +
                     "WHERE u.email = ? AND c.course_id = ? AND en.status = 'ACTIVE' LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MyCourseBean.class), email, courseId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 🟢 3. FETCH INDIVIDUAL CLASSROOM TIMELINE SYLLABUS LIST
     */
    public List<LessonProgressBean> getCourseLessonTimeline(int courseId, String batchCode) {
        String sql = "SELECT l.lesson_id AS lessonId, l.title AS lessonTitle, l.content_type AS contentType, " +
                     "l.duration_min AS durationMin, m.title AS moduleTitle " +
                     "FROM lms_db.lessons l " +
                     "JOIN lms_db.course_modules m ON l.module_id = m.module_id " +
                     "JOIN lms_db.batches b ON b.course_id = m.course_id " +
                     "WHERE b.course_id = ? AND b.batch_code = ? AND l.status = 'PUBLISHED' " +
                     "ORDER BY m.module_id ASC, l.lesson_id ASC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LessonProgressBean.class), courseId, batchCode);
    }

    /**
     * 🟢 4. SEPARATE PROGRESS ENGINE FOR LESSONS
     * Marks a specific lesson as completed uniquely for the logged-in student's email context string.
     */
    public void markLessonAsCompleted(int batchId, int lessonId, String studentEmail) {
        String lessonTitleSql = "SELECT title FROM lms_db.lessons WHERE lesson_id = ? LIMIT 1";
        String plainLessonTitle;
        try {
            plainLessonTitle = jdbcTemplate.queryForObject(lessonTitleSql, String.class, lessonId);
        } catch (Exception e) {
            plainLessonTitle = "Completed Student Session";
        }

        // Form an iron-clad compound title token to isolate this student from peers
        String isolatedSessionTitle = plainLessonTitle + " [" + studentEmail + "]";

        String checkSql = "SELECT COUNT(*) FROM lms_db.class_sessions WHERE batch_id = ? AND lesson_id = ? AND title = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, batchId, lessonId, isolatedSessionTitle);

        if (count != null && count > 0) {
            String updateSql = "UPDATE lms_db.class_sessions SET status = 'DONE' WHERE batch_id = ? AND lesson_id = ? AND title = ?";
            jdbcTemplate.update(updateSql, batchId, lessonId, isolatedSessionTitle);
        } else {
            // Passes NOW() directly to satisfy mandatory non-nullable database table columns
            String insertSql = "INSERT INTO lms_db.class_sessions (batch_id, lesson_id, title, start_at, end_at, status) VALUES (?, ?, ?, NOW(), NOW(), 'DONE')";
            jdbcTemplate.update(insertSql, batchId, lessonId, isolatedSessionTitle);
        }
    }

    /**
     * 🟢 5. REGRESSION PROGRESS ENGINE CLEANUP
     * Erases a student's completed lesson log using their text suffix context.
     */
    public void regressLessonProgress(int batchId, int lessonId, String studentEmail) {
        String lessonTitleSql = "SELECT title FROM lms_db.lessons WHERE lesson_id = ? LIMIT 1";
        String plainLessonTitle;
        try {
            plainLessonTitle = jdbcTemplate.queryForObject(lessonTitleSql, String.class, lessonId);
        } catch (Exception e) {
            plainLessonTitle = "";
        }

        String isolatedSessionTitle = plainLessonTitle + " [" + studentEmail + "]";

        String deleteSql = "DELETE FROM lms_db.class_sessions WHERE batch_id = ? AND lesson_id = ? AND title = ?";
        jdbcTemplate.update(deleteSql, batchId, lessonId, isolatedSessionTitle);
    }

    /**
     * 🟢 6. MANUALLY PROCESS TOTAL COURSE GRADUATION STATE SHIFT
     */
    public boolean finalizeCourseManualCheckout(String email, int courseId) {
        String updateSql = "UPDATE lms_db.enrollments e " +
                           "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                           "JOIN lms_db.users u ON e.user_id = u.user_id " +
                           "SET e.status = 'COMPLETED', e.completed_at = NOW() " +
                           "WHERE u.email = ? AND b.course_id = ? AND e.status = 'ACTIVE'";
                           
        int rowsAffected = jdbcTemplate.update(updateSql, email, courseId);
        return rowsAffected > 0;
    }
}
