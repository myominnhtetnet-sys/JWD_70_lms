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
        String sql = "SELECT b.batch_id, b.batch_code, b.title AS batch_title, b.start_date, b.end_date, " +
                     "b.schedule_note, c.course_name " +
                     "FROM enrollments e " +
                     "JOIN batches b ON e.batch_id = b.batch_id " +
                     "LEFT JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE e.user_id = ?";
        try {
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getClassSessions(int userId) {
        String sql = "SELECT cs.session_date, cs.start_time, cs.end_time, cs.topic, b.title AS batch_title " +
                     "FROM class_sessions cs " +
                     "JOIN enrollments e ON cs.batch_id = e.batch_id " +
                     "JOIN batches b ON cs.batch_id = b.batch_id " +
                     "WHERE e.user_id = ? " +
                     "ORDER BY cs.session_date ASC, cs.start_time ASC";
        try {
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getStudentExams(int userId) {
        String sql = "SELECT ex.title, ex.exam_date, ex.duration_minutes, c.course_name " +
                     "FROM exams ex " +
                     "JOIN batches b ON ex.course_id = b.course_id " +
                     "JOIN enrollments e ON b.batch_id = e.batch_id " +
                     "LEFT JOIN courses c ON ex.course_id = c.course_id " +
                     "WHERE e.user_id = ? " +
                     "ORDER BY ex.exam_date ASC";
        try {
            return jdbcTemplate.queryForList(sql, userId);
        } catch (Exception e) {
            return List.of();
        }
    }
}