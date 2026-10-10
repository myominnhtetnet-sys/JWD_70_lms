package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MyCourseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 🟢 Pulls all enrolled courses and calculates progress percentage bar values
     * strictly by reading DONE class sessions based on the student's active email.
     */
    public List<MyCourseBean> getEnrolledCoursesProgressByEmail(String email) {
        String sql = 
            "SELECT " +
            "    c.course_id, " +
            "    c.title AS course_title, " +
            "    b.batch_code, " +
            "    b.status AS batch_status, " +
            /* 🟢 FIXED: Changed u.name to u.full_name to match your users table columns exactly */
            "    COALESCE(u.full_name, 'Staff Instructor') AS teacher_name, " +
            "    (" +
            "        SELECT COUNT(DISTINCT cs.lesson_id) " +
            "        FROM lms_db.class_sessions cs " +
            "        WHERE cs.batch_id = b.batch_id AND cs.status = 'DONE' AND cs.lesson_id IS NOT NULL" +
            "    ) AS completed_lessons, " +
            "    (" +
            "        SELECT COUNT(DISTINCT l.lesson_id) " +
            "        FROM lms_db.course_modules m " +
            "        JOIN lms_db.lessons l ON l.module_id = m.module_id " +
            "        WHERE m.course_id = c.course_id AND l.status = 'PUBLISHED'" +
            "    ) AS total_lessons " +
            "FROM lms_db.enrollments e " +
            "JOIN lms_db.users su ON e.user_id = su.user_id " +
            "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
            "JOIN lms_db.courses c ON b.course_id = c.course_id " +
            "LEFT JOIN lms_db.course_instructors ci ON ci.course_id = c.course_id AND ci.is_primary = 1 " +
            "LEFT JOIN lms_db.users u ON ci.instructor_id = u.user_id " +
            "WHERE su.email = ? AND e.status = 'ACTIVE'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MyCourseBean bean = new MyCourseBean();
            bean.setCourseId(rs.getInt("course_id"));
            bean.setCourseTitle(rs.getString("course_title"));
            bean.setBatchCode(rs.getString("batch_code"));
            bean.setBatchStatus(rs.getString("batch_status"));
            bean.setTeacherName(rs.getString("teacher_name"));
            
            int completed = rs.getInt("completed_lessons");
            int total = rs.getInt("total_lessons");
            
            bean.setCompletedLessons(completed);
            bean.setTotalLessons(total);
            bean.setProgressPercent((total > 0) ? (completed * 100) / total : 0);
            
            return bean;
        }, email);
    }
    
    /**
     * 🟢 Fetches individual course title and metadata for the classroom header block.
     */
    public MyCourseBean getCourseMetaDetails(int courseId, String email) {
        String sql = 
            "SELECT c.course_id, c.title AS course_title, b.batch_code, b.status AS batch_status, " +
            "COALESCE(u.full_name, 'Staff Instructor') AS teacher_name " +
            "FROM lms_db.enrollments e " +
            "JOIN lms_db.users su ON e.user_id = su.user_id " +
            "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
            "JOIN lms_db.courses c ON b.course_id = c.course_id " +
            "LEFT JOIN lms_db.course_instructors ci ON ci.course_id = c.course_id AND ci.is_primary = 1 " +
            "LEFT JOIN lms_db.users u ON ci.instructor_id = u.user_id " +
            "WHERE c.course_id = ? AND su.email = ? AND e.status = 'ACTIVE'";
            
        List<MyCourseBean> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            MyCourseBean bean = new MyCourseBean();
            bean.setCourseId(rs.getInt("course_id"));
            bean.setCourseTitle(rs.getString("course_title"));
            bean.setBatchCode(rs.getString("batch_code"));
            bean.setBatchStatus(rs.getString("batch_status"));
            bean.setTeacherName(rs.getString("teacher_name"));
            return bean;
        }, courseId, email);
        
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 🟢 Pulls all published lessons ordered by module sorting, checking real-time 
     * class session status rows to mark completions automatically.
     */
    public List<LessonProgressBean> getCourseLessonTimeline(int courseId, String batchCode) {
        String sql = 
            "SELECT l.lesson_id, m.title AS module_title, l.title AS lesson_title, l.content_type, l.duration_min, " +
            "EXISTS(" +
            "    SELECT 1 FROM lms_db.class_sessions cs " +
            "    JOIN lms_db.batches b ON cs.batch_id = b.batch_id " +
            "    WHERE b.batch_code = ? AND cs.lesson_id = l.lesson_id AND cs.status = 'DONE'" +
            ") AS is_done " +
            "FROM lms_db.course_modules m " +
            "JOIN lms_db.lessons l ON l.module_id = m.module_id " +
            "WHERE m.course_id = ? AND l.status = 'PUBLISHED' " +
            "ORDER BY m.sort_order ASC, l.sort_order ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
          LessonProgressBean bean = 
                new LessonProgressBean();
            bean.setLessonId(rs.getInt("lesson_id"));
            bean.setModuleTitle(rs.getString("module_title"));
            bean.setLessonTitle(rs.getString("lesson_title"));
            bean.setContentType(rs.getString("content_type"));
            bean.setDurationMin(rs.getInt("duration_min"));
            bean.setDone(rs.getBoolean("is_done"));
            return bean;
        }, batchCode, courseId);
    }

}
