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
        try {
         
            String sql = """
                SELECT 
                    ex.exam_id,
                    ex.exam_title AS title,
                    c.title AS course_name,
                    ex.exam_date,
                    ex.duration_minutes
                FROM enrollments e
                JOIN batches b ON e.batch_id = b.batch_id
                JOIN courses c ON b.course_id = c.course_id
                JOIN exams ex ON c.course_id = ex.course_id
                WHERE e.user_id = ?
                ORDER BY ex.exam_date ASC
                """;
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
          
            return new java.util.ArrayList<>();
        }
    }
}