package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Teacher_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Get active batches assigned to this instructor
    public List<Map<String, Object>> getBatchesByInstructor(int instructorId) {
        String sql = "SELECT b.batch_id, b.batch_code, b.title, b.start_date, b.end_date, " +
                     "b.schedule_note, b.max_seats, b.status, c.title AS course_title " +
                     "FROM batches b " +
                     "JOIN batch_instructors bi ON b.batch_id = bi.batch_id " +
                     "JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE bi.instructor_id = ?";
        return jdbcTemplate.queryForList(sql, instructorId);
    }

    // Get courses assigned to this instructor
    public List<Map<String, Object>> getCoursesByInstructor(int instructorId) {
        String sql = "SELECT c.course_id, c.title, c.slug, c.level, c.status, c.short_description, " +
                     "COALESCE(ci.is_primary, 0) AS is_primary " +
                     "FROM courses c " +
                     "JOIN course_instructors ci ON c.course_id = ci.course_id " +
                     "WHERE ci.instructor_id = ?";
        return jdbcTemplate.queryForList(sql, instructorId);
    }

    // Get single batch details by batch ID
    public Map<String, Object> getBatchById(Long batchId) {
        String sql = "SELECT b.batch_id, b.batch_code, b.title, b.start_date, b.end_date, " +
                     "b.schedule_note, b.max_seats, b.status, c.title AS course_title " +
                     "FROM batches b " +
                     "JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE b.batch_id = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, batchId);
        return result.isEmpty() ? null : result.get(0);
    }

    // Get students enrolled in a specific batch (with explicit column aliases)
    public List<Map<String, Object>> getStudentsByBatch(Long batchId) {
        String sql = "SELECT u.user_id, u.full_name AS full_name, u.email AS email, " +
                     "e.enrolled_at AS enrolled_at, e.status AS status " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "WHERE e.batch_id = ?";
        return jdbcTemplate.queryForList(sql, batchId);
    }

 // Fetch class sessions (dates) for a specific batch
    public List<Map<String, Object>> getSessionsByBatch(Long batchId) {
        String sql = "SELECT session_id, batch_id, title, start_at, end_at, status " +
                     "FROM class_sessions WHERE batch_id = ? ORDER BY start_at DESC";
        return jdbcTemplate.queryForList(sql, batchId);
    }

    // Get active roster with enrollment IDs for attendance
    public List<Map<String, Object>> getRosterByBatch(Long batchId) {
        String sql = "SELECT e.enrollment_id, u.user_id, u.full_name AS full_name, u.email AS email, " +
                     "e.enrolled_at AS enrolled_at, e.status AS status " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "WHERE e.batch_id = ? AND e.status = 'ACTIVE'";
        return jdbcTemplate.queryForList(sql, batchId);
    }

 // Save or update attendance records for a specific session date
    public void saveAttendance(Long sessionId, Long enrollmentId, String status, int markedBy) {
        String sql = "INSERT INTO attendance (session_id, enrollment_id, status, marked_by) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = ?";
        jdbcTemplate.update(sql, sessionId, enrollmentId, status, markedBy, status);
    }
	/*
	 * // Get materials for a specific lesson public List<Map<String, Object>>
	 * getMaterialsByLesson(Long lessonId) { String sql =
	 * "SELECT material_id, lesson_id, title, file_path, file_type, file_size_kb, created_at "
	 * + "FROM lesson_materials WHERE lesson_id = ? ORDER BY created_at DESC";
	 * return jdbcTemplate.queryForList(sql, lessonId); }
	 * 
	 * // Save uploaded material into lesson_materials public void
	 * saveLessonMaterial(Long lessonId, String title, String filePath, String
	 * fileType, long fileSizeKb) { String sql =
	 * "INSERT INTO lesson_materials (lesson_id, title, file_path, file_type, file_size_kb) "
	 * + "VALUES (?, ?, ?, ?, ?)"; jdbcTemplate.update(sql, lessonId, title,
	 * filePath, fileType, fileSizeKb); }
	 */
}