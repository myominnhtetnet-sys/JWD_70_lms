package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

   
    public List<Map<String, Object>> getStudentBatches(int userId) {
        String sql = """
            SELECT 
                b.batch_id,
                b.batch_code,
                b.title AS batch_title,
                b.schedule_note,
                b.start_date,
                b.end_date,
                b.status
            FROM enrollments e
            JOIN batches b ON e.batch_id = b.batch_id
            WHERE e.user_id = ?
            """;

        return jdbcTemplate.queryForList(sql, userId);
    }

    
    public List<Map<String, Object>> getClassSessions(int userId) {
        String sql = """
            SELECT 
                cs.session_id,
                b.title AS batch_title,
                cs.title AS topic,
                DATE(cs.start_at) AS session_date,
                TIME(cs.start_at) AS start_time,
                TIME(cs.end_at) AS end_time
            FROM enrollments e
            JOIN class_sessions cs ON e.batch_id = cs.batch_id
            JOIN batches b ON e.batch_id = b.batch_id
            WHERE e.user_id = ?
            ORDER BY cs.start_at ASC
            """;

        return jdbcTemplate.queryForList(sql, userId);
    }

    public List<Map<String, Object>> getStudentExams(int userId) {
        // 🟢 FIXED: Added raw 'e.start_at' directly into the SELECT list to completely resolve MySQL Error 3065
        // 🟢 ALIAS SYNC: Maps both uppercase and lowercase columns for view context mapping safety
        String sql = """
            SELECT DISTINCT
                e.exam_id,
                e.exam_id AS EXAM_ID,
                e.title,
                e.title AS TITLE,
                e.duration_min,
                e.duration_min AS DURATION_MIN,
                e.start_at,
                COALESCE(DATE_FORMAT(e.start_at, '%Y-%m-%d %H:%i'), 'Flexible Window') AS formatted_start_at
            FROM exams e
            INNER JOIN enrollments en ON e.batch_id = en.batch_id
            WHERE en.user_id = ? 
              AND LOWER(en.status) = 'active'
            ORDER BY e.start_at ASC
            """;
        try {
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            System.err.println("❌ CRITICAL: getStudentExams SQL execution crashed: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

}