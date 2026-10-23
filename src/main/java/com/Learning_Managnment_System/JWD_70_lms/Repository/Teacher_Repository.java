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

 // Get single batch details by batch ID, including the course level
    public Map<String, Object> getBatchById(Long batchId) {
        String sql = "SELECT b.batch_id, b.batch_code, b.title, b.start_date, b.end_date, " +
                     "b.schedule_note, b.max_seats, b.status, c.title AS course_title, c.level AS course_level " +
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
        String sql = "INSERT INTO attendance (session_id, enrollment_id, status, marked_by, marked_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "status = ?, " +
                     "marked_by = ?, " +
                     "marked_at = CURRENT_TIMESTAMP";
        
        jdbcTemplate.update(sql, sessionId, enrollmentId, status, markedBy, status, markedBy);
    }

    // Fetch saved attendance records for a specific session date
    public List<Map<String, Object>> getAttendanceBySession(Long sessionId) {
        String sql = "SELECT a.attendance_id, a.session_id, a.enrollment_id, a.status, a.marked_at, " +
                     "u.full_name, u.email " +
                     "FROM attendance a " +
                     "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "WHERE a.session_id = ?";
        return jdbcTemplate.queryForList(sql, sessionId);
    }

    // Get attendance summary percentage and custom progress status for all active students in a batch
    public List<Map<String, Object>> getBatchAttendanceSummary(Long batchId) {
        String sql = "SELECT e.enrollment_id, u.user_id, u.full_name AS full_name, u.email AS email, " +
                     "e.enrolled_at AS enrolled_at, e.status AS enrollment_status, e.status_reason, " +
                     "COUNT(s.session_id) AS total_sessions, " +
                     "COALESCE(SUM(CASE a.status " +
                     "   WHEN 'PRESENT' THEN 1.0 " +
                     "   WHEN 'LATE' THEN 0.5 " +
                     "   WHEN 'EXCUSED' THEN 0.5 " +
                     "   ELSE 0.0 " +
                     "END), 0.0) AS earned_points, " +
                     "ROUND((COALESCE(SUM(CASE a.status " +
                     "   WHEN 'PRESENT' THEN 1.0 " +
                     "   WHEN 'LATE' THEN 0.5 " +
                     "   WHEN 'EXCUSED' THEN 0.5 " +
                     "   ELSE 0.0 " +
                     "END), 0.0) / NULLIF(COUNT(s.session_id), 0)) * 100, 2) AS attendance_percentage " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN class_sessions s ON s.batch_id = e.batch_id " +
                     "LEFT JOIN attendance a ON a.session_id = s.session_id AND a.enrollment_id = e.enrollment_id " +
                     "WHERE e.batch_id = ? AND e.status = 'ACTIVE' " +
                     "GROUP BY e.enrollment_id, u.user_id, u.full_name, u.email, e.enrolled_at, e.status, e.status_reason";
        
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, batchId);
        
        // Extract progress status safely for batch detail view badges
        for (Map<String, Object> row : rows) {
            String statusReason = (String) row.get("status_reason");
            String progressStatus = "ACTIVE";
            if (statusReason != null && statusReason.contains("|")) {
                progressStatus = statusReason.split("\\|", 2)[0];
            } else if (statusReason != null && (statusReason.equals("ACTIVE") || statusReason.equals("PASSIVE") || statusReason.equals("IDLE") || statusReason.equals("INACTIVE"))) {
                progressStatus = statusReason;
            }
            row.put("student_progress_status", progressStatus);
        }
        
        return rows;
    }
 // Get basic student & batch details for the progress view
    public Map<String, Object> getStudentEnrollmentDetails(Long enrollmentId) {
        String sql = "SELECT e.enrollment_id, e.batch_id, u.user_id, u.full_name, u.email, " +
                     "b.batch_code, b.title AS batch_title, c.title AS course_title, " +
                     "b.status AS batch_status, e.status_reason " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.user_id " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE e.enrollment_id = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, enrollmentId);
        if (result.isEmpty()) return null;
        
        Map<String, Object> row = result.get(0);
        String statusReason = (String) row.get("status_reason");
        
        String progressStatus = "ACTIVE";
        String teacherNote = "";
        
        if (statusReason != null && statusReason.contains("|")) {
            String[] parts = statusReason.split("\\|", 2);
            progressStatus = parts[0];
            teacherNote = parts.length > 1 ? parts[1] : "";
        } else if (statusReason != null && (statusReason.equals("ACTIVE") || statusReason.equals("PASSIVE") || statusReason.equals("IDLE") || statusReason.equals("INACTIVE"))) {
            progressStatus = statusReason;
        } else {
            teacherNote = statusReason != null ? statusReason : "";
        }
        
        row.put("student_progress_status", progressStatus);
        row.put("teacher_note", teacherNote);
        
        return row;
    }
    // Get all sessions for the student's batch, along with their attendance status for each session
    public List<Map<String, Object>> getStudentSessionAttendanceList(Long batchId, Long enrollmentId) {
        String sql = "SELECT s.session_id, s.title AS session_title, s.start_at, s.status AS session_status, " +
                     "a.status AS attendance_status, a.marked_at " +
                     "FROM class_sessions s " +
                     "LEFT JOIN attendance a ON s.session_id = a.session_id AND a.enrollment_id = ? " +
                     "WHERE s.batch_id = ? " +
                     "ORDER BY s.start_at DESC";
        return jdbcTemplate.queryForList(sql, enrollmentId, batchId);
    }

    // Save both student progress status and teacher note together into status_reason
    public void updateStudentProgressAndNote(Long enrollmentId, String progressStatus, String teacherNote) {
        String combined = progressStatus + "|" + (teacherNote != null ? teacherNote : "");
        String sql = "UPDATE enrollments SET status_reason = ? WHERE enrollment_id = ?";
        jdbcTemplate.update(sql, combined, enrollmentId);
    }
}