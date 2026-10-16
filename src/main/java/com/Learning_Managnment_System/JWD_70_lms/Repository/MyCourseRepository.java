package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MyCourseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findEnrolledCoursesProgressByEmail(String email) {
        String sql = "SELECT c.course_id, b.batch_id, c.title AS course_title, b.batch_code, b.status AS batch_status, " +
                     "COALESCE(u.full_name, 'Staff Instructor') AS teacher_name, " +
                     "( " +
                     "  SELECT COUNT(DISTINCT cs.lesson_id) " +
                     "  FROM lms_db.class_sessions cs " +
                     "  WHERE cs.batch_id = b.batch_id " +
                     "  AND cs.status = 'DONE' " +
                     "  AND cs.title LIKE CONCAT('% [', ?, ']') " + 
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

    public MyCourseBean findCourseMetaDetails(int courseId, String email) {
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

    public List<LessonProgressBean> findCourseLessonTimeline(int courseId, String batchCode, String email) {
        // 🟢 FIXED: Subquery now targets the explicitly passed logged-in student's email token
        String sql = "SELECT l.lesson_id AS lessonId, l.title AS lessonTitle, l.content_type AS contentType, " +
                     "l.duration_min AS durationMin, m.title AS moduleTitle, " +
                     "( " +
                     "  SELECT COUNT(*) FROM lms_db.class_sessions cs " +
                     "  WHERE cs.batch_id = b.batch_id AND cs.lesson_id = l.lesson_id " +
                     "  AND cs.title = CONCAT(l.title, ' [', ?, ']') AND cs.status = 'DONE' " +
                     ") > 0 AS done " +
                     "FROM lms_db.lessons l " +
                     "JOIN lms_db.course_modules m ON l.module_id = m.module_id " +
                     "JOIN lms_db.batches b ON b.course_id = m.course_id " +
                     "WHERE b.course_id = ? AND b.batch_code = ? AND l.status = 'PUBLISHED' " +
                     "ORDER BY m.module_id ASC, l.lesson_id ASC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LessonProgressBean.class), email, courseId, batchCode);
    }


    // 🟢 FIXED: Restored your clean original SQL query rules to completely bypass columns like file_path
    public Map<String, Object> findActiveLessonCoreMap(int lessonId) {
        String sql = "SELECT l.lesson_id AS lessonId, l.lesson_id AS lesson_id, l.title AS lessonTitle, " +
                     "l.content_type AS contentType, m.title AS moduleTitle, l.duration_min AS durationMin " +
                     "FROM lms_db.lessons l " +
                     "JOIN lms_db.course_modules m ON l.module_id = m.module_id " +
                     "WHERE l.lesson_id = ? LIMIT 1";
        return jdbcTemplate.queryForMap(sql, lessonId);
    }

    public Map<String, Object> findLessonExtraFields(int lessonId) {
        String sql = "SELECT video_url, content FROM lms_db.lessons WHERE lesson_id = ?";
        return jdbcTemplate.queryForMap(sql, lessonId);
    }

    public Integer checkIsLessonDone(int courseId, int lessonId, String lessonTitle, String email) {
        String sql = "SELECT COUNT(*) FROM lms_db.class_sessions cs " +
                     "JOIN lms_db.batches b ON cs.batch_id = b.batch_id " +
                     "WHERE b.course_id = ? AND cs.lesson_id = ? " +
                     "AND cs.title = CONCAT(?, ' [', ?, ']') AND cs.status = 'DONE'";
        return jdbcTemplate.queryForObject(sql, Integer.class, courseId, lessonId, lessonTitle, email);
    }

    public Map<String, Object> findCourseWorkspaceMetadata(String email, int courseId) {
        String sql = "SELECT c.course_id AS courseId, c.course_id AS course_id, c.title AS courseTitle, b.batch_id AS batchId " +
                     "FROM lms_db.courses c " +
                     "JOIN lms_db.batches b ON b.course_id = c.course_id " +
                     "JOIN lms_db.enrollments en ON en.batch_id = b.batch_id " +
                     "JOIN lms_db.users u ON en.user_id = u.user_id " +
                     "WHERE u.email = ? AND c.course_id = ? AND en.status = 'ACTIVE' LIMIT 1";
        return jdbcTemplate.queryForMap(sql, email, courseId);
    }

    public List<LessonProgressBean> findSidebarLessons(String email, int courseId) {
        String sql = "SELECT l.lesson_id AS lessonId, l.title AS lessonTitle, l.content_type AS contentType, l.duration_min AS durationMin, " +
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
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LessonProgressBean.class), email, email, courseId);
    }

    public Optional<Integer> findActiveBatchId(String email, int courseId) {
        String sql = "SELECT e.batch_id FROM lms_db.enrollments e " +
                     "JOIN lms_db.users u ON e.user_id = u.user_id " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "WHERE u.email = ? AND b.course_id = ? AND e.status = 'ACTIVE' LIMIT 1";
        try {
            Integer batchId = jdbcTemplate.queryForObject(sql, Integer.class, email, courseId);
            return Optional.ofNullable(batchId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public String findLessonTitleById(int lessonId) {
        String sql = "SELECT title FROM lms_db.lessons WHERE lesson_id = ? LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class, lessonId);
    }

    public void insertClassSession(int batchId, int lessonId, String sessionTitle) {
        String sql = "INSERT INTO lms_db.class_sessions (batch_id, lesson_id, title, start_at, end_at, status) VALUES (?, ?, ?, NOW(), NOW(), 'DONE')";
        jdbcTemplate.update(sql, batchId, lessonId, sessionTitle);
    }

    public void deleteClassSession(int batchId, int lessonId, String sessionTitle) {
        String sql = "DELETE FROM lms_db.class_sessions WHERE batch_id = ? AND lesson_id = ? AND title = ?";
        jdbcTemplate.update(sql, batchId, lessonId, sessionTitle);
    }

    public boolean updateEnrollmentToCompleted(String email, int courseId) {
        String sql = "UPDATE lms_db.enrollments e " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN lms_db.users u ON e.user_id = u.user_id " +
                     "SET e.status = 'COMPLETED', e.completed_at = NOW() " +
                     "WHERE u.email = ? AND b.course_id = ? AND e.status = 'ACTIVE'";
        int rowsAffected = jdbcTemplate.update(sql, email, courseId);
        return rowsAffected > 0;
    }
}
