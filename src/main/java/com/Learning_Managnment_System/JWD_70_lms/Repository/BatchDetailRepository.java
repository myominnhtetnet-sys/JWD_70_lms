package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchDetailMappers.*;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchDetailBean.*;

@Repository
public class BatchDetailRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BatchInfo> findAllBatches() {
        String sql = "SELECT batch_id, batch_code, title FROM batches ORDER BY start_date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BatchInfo b = new BatchInfo();
            b.setBatchId(rs.getLong("batch_id"));
            b.setBatchCode(rs.getString("batch_code"));
            b.setTitle(rs.getString("title"));
            return b;
        });
    }

    // 1. Enrolled Students 
    public List<StudentInfo> findStudentsByBatchId(Long batchId) {
        String sql = """
            SELECT e.enrollment_id, e.user_id, u.full_name, u.email, e.payment_status, e.status, e.enrolled_at
            FROM enrollments e
            JOIN users u ON e.user_id = u.user_id
            JOIN roles r ON u.role_id = r.role_id
            WHERE e.batch_id = ? AND r.role_name = 'Student'
            ORDER BY e.enrolled_at DESC
        """;
        return jdbcTemplate.query(sql, new BatchStudentRowMapper(), batchId);
    }

    // 2. Class Sessions
    public List<ClassSessionInfo> findSessionsByBatchId(Long batchId) {
        String sql = """
            SELECT session_id, batch_id, lesson_id, title, start_at, end_at, meeting_url, status
            FROM class_sessions
            WHERE batch_id = ?
            ORDER BY start_at ASC
        """;
        return jdbcTemplate.query(sql, new ClassSessionRowMapper(), batchId);
    }

    public int saveSession(Long batchId, ClassSessionInfo sessionInfo) {
        String sql = """
            INSERT INTO class_sessions (batch_id, lesson_id, title, start_at, end_at, meeting_url, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        return jdbcTemplate.update(sql,
            batchId,
            sessionInfo.getLessonId(),
            sessionInfo.getTitle(),
            sessionInfo.getStartAt(),
            sessionInfo.getEndAt(),
            sessionInfo.getMeetingUrl(),
            sessionInfo.getStatus() != null ? sessionInfo.getStatus() : "SCHEDULED"
        );
    }

    // 3. Student Progress per Batch 
    public List<StudentProgress> findStudentProgressByBatchId(Long batchId) {
        String sql = """
            SELECT e.enrollment_id, u.full_name,
                   COUNT(CASE WHEN lp.status = 'COMPLETED' THEN 1 END) as completed_lessons,
                   COUNT(lp.lesson_id) as total_lessons,
                   COALESCE(AVG(lp.progress_percent), 0.0) as avg_progress
            FROM enrollments e
            JOIN users u ON e.user_id = u.user_id
            JOIN roles r ON u.role_id = r.role_id
            LEFT JOIN lesson_progress lp ON e.enrollment_id = lp.enrollment_id
            WHERE e.batch_id = ? AND r.role_name = 'Student'
            GROUP BY e.enrollment_id, u.full_name
        """;
        return jdbcTemplate.query(sql, new StudentProgressRowMapper(), batchId);
    }

    // 4. Batch Announcements
    public List<AnnouncementInfo> findAnnouncementsByBatchId(Long batchId) {
        String sql = """
            SELECT a.announcement_id, a.batch_id, a.title, a.body, u.full_name as posted_by_name, a.published_at
            FROM announcements a
            JOIN users u ON a.posted_by = u.user_id
            WHERE a.batch_id = ?
            ORDER BY a.published_at DESC
        """;
        return jdbcTemplate.query(sql, new BatchAnnouncementRowMapper(), batchId);
    }
	
    public List<LessonInfo> findAllLessons() {
        String sql = "SELECT lesson_id, title FROM lessons ORDER BY lesson_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LessonInfo l = new LessonInfo();
            l.setLessonId(rs.getLong("lesson_id"));
            l.setTitle(rs.getString("title"));
            return l;
        });
    }
}