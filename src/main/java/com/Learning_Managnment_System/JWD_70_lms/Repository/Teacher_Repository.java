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

    // Get active batches assigned to this instructor[cite: 1]
    public List<Map<String, Object>> getBatchesByInstructor(int instructorId) {
        String sql = "SELECT b.batch_id, b.batch_code, b.title, b.start_date, b.end_date, " +
                     "b.schedule_note, b.max_seats, b.status, c.title AS course_title " +
                     "FROM batches b " +
                     "JOIN batch_instructors bi ON b.batch_id = bi.batch_id " +
                     "JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE bi.instructor_id = ?";
        return jdbcTemplate.queryForList(sql, instructorId);
    }

    // Get courses assigned to this instructor[cite: 1]
    public List<Map<String, Object>> getCoursesByInstructor(int instructorId) {
    	String sql = "SELECT " +
                "    c.course_id, " +
                "    c.title AS course_title, " +
                "    b.batch_code, " +
                "    b.status AS batch_status, " +
                "    COALESCE(u.full_name, 'Staff Instructor') AS teacher_name, " +
                "    ( " +
                "        SELECT COUNT(DISTINCT cs.lesson_id) " +
                "        FROM class_sessions cs " +
                "        WHERE cs.batch_id = b.batch_id AND cs.status = 'DONE' AND cs.lesson_id IS NOT NULL " +
                "    ) AS completed_lessons, " +
                "    ( " +
                "        SELECT COUNT(DISTINCT l.lesson_id) " +
                "        FROM course_modules m " +
                "        JOIN lessons l ON l.module_id = m.module_id " +
                "        WHERE m.course_id = c.course_id AND l.status = 'PUBLISHED' " +
                "    ) AS total_lessons " +
                "FROM enrollments e " +
                "JOIN users su ON e.user_id = su.user_id " +
                "JOIN batches b ON e.batch_id = b.batch_id " +
                "JOIN courses c ON b.course_id = c.course_id " +
                "LEFT JOIN course_instructors ci ON ci.course_id = c.course_id AND ci.is_primary = 1 " +
                "LEFT JOIN users u ON ci.instructor_id = u.user_id " +
                "WHERE su.email = ? AND e.status = 'ACTIVE'";
        return jdbcTemplate.queryForList(sql, instructorId);
    }
}