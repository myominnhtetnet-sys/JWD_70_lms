package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.AssignmentRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.SubmissionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Repository
public class StudentAssignmentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssignmentRowMapper assignmentRowMapper;

    @Autowired
    private SubmissionRowMapper submissionRowMapper;

    public List<AssignmentBean> findAssignmentsByStudentId(int userId) {
        String sql = """
                SELECT a.*, b.batch_code AS batch_code, b.title AS batch_title, l.title AS lesson_title
                FROM assignments a
                JOIN batches b ON a.batch_id = b.batch_id
                JOIN enrollments e ON b.batch_id = e.batch_id
                LEFT JOIN lessons l ON a.lesson_id = l.lesson_id
                WHERE e.user_id = ? AND a.status = 'PUBLISHED' AND e.status = 'ACTIVE'
                ORDER BY a.due_at ASC
                """;
        return jdbcTemplate.query(sql, assignmentRowMapper, userId);
    }

    public Optional<AssignmentBean> findAssignmentById(Integer assignmentId) {
        String sql = "SELECT * FROM assignments WHERE assignment_id = ? AND status = 'PUBLISHED'";
        List<AssignmentBean> result = jdbcTemplate.query(sql, assignmentRowMapper, assignmentId);
        return result.stream().findFirst();
    }

    public List<SubmissionBean> findSubmittedAssignments(int userId) {
        String sql = """
                SELECT s.*, s.mark AS score, 
                       a.title AS assignment_title, 
                       a.total_mark AS total_mark,
                       u.full_name AS student_name
                FROM submissions s
                JOIN enrollments e ON s.enrollment_id = e.enrollment_id
                JOIN assignments a ON s.assignment_id = a.assignment_id
                JOIN users u ON e.user_id = u.user_id
                WHERE e.user_id = ?
                """;
        return jdbcTemplate.query(sql, submissionRowMapper, userId);
    }

    public List<SubmissionBean> findLateAssignments(int userId) {
        String sql = """
                SELECT DISTINCT s.*, s.mark AS score,
                                a.title AS assignment_title,
                                a.total_mark AS total_mark,
                                u.full_name AS student_name
                FROM submissions s
                JOIN enrollments e ON s.enrollment_id = e.enrollment_id
                JOIN assignments a ON s.assignment_id = a.assignment_id
                JOIN users u ON e.user_id = u.user_id
                WHERE e.user_id = ? AND s.is_late = 1
                """;
        return jdbcTemplate.query(sql, submissionRowMapper, userId);
    }
    
    // 🟢 OVERLOADED: Count total active assignments matching active filter keywords
    public int getTotalAssignmentsCount(int userId, String title, String status) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM assignments a
                JOIN batches b ON a.batch_id = b.batch_id
                JOIN enrollments e ON b.batch_id = e.batch_id
                WHERE e.user_id = ? AND a.status = 'PUBLISHED' AND e.status = 'ACTIVE'
                """);
        List<Object> params = new ArrayList<>();
        params.add(userId);

        buildAssignmentDynamicQuery(title, status, userId, sql, params);

        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    // 🟢 OVERLOADED: Fetch chunked page cards using dynamic parameters and OFFSET slicing
    public List<AssignmentBean> findPaginatedAssignmentsByStudentId(int userId, String title, String status, int page, int pageSize) {
        int offset = page * pageSize;
        
        StringBuilder sql = new StringBuilder("""
                SELECT a.*, b.batch_code AS batch_code, b.title AS batch_title, l.title AS lesson_title
                FROM assignments a
                JOIN batches b ON a.batch_id = b.batch_id
                JOIN enrollments e ON b.batch_id = e.batch_id
                LEFT JOIN lessons l ON a.lesson_id = l.lesson_id
                WHERE e.user_id = ? AND a.status = 'PUBLISHED' AND e.status = 'ACTIVE'
                """);
        List<Object> params = new ArrayList<>();
        params.add(userId);

        buildAssignmentDynamicQuery(title, status, userId, sql, params);

        sql.append(" ORDER BY a.due_at ASC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), assignmentRowMapper, params.toArray());
    }

    // 🟢 NEW PRIVATE HELPER: Dynamically handles and appends assignment database filtering rules
    private void buildAssignmentDynamicQuery(String title, String status, int userId, StringBuilder sql, List<Object> params) {
        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND LOWER(a.title) LIKE ?");
            params.add("%" + title.trim().toLowerCase() + "%");
        }
        
        if (status != null && !status.trim().isEmpty()) {
            if ("SUBMITTED".equalsIgnoreCase(status)) {
                sql.append("""
                     AND EXISTS (
                         SELECT 1 FROM submissions sub 
                         JOIN enrollments en ON sub.enrollment_id = en.enrollment_id 
                         WHERE sub.assignment_id = a.assignment_id AND en.user_id = ? AND sub.is_late = 0
                     )
                     """);
                params.add(userId);
            } 
            else if ("LATE".equalsIgnoreCase(status)) {
                sql.append("""
                     AND EXISTS (
                         SELECT 1 FROM submissions sub 
                         JOIN enrollments en ON sub.enrollment_id = en.enrollment_id 
                         WHERE sub.assignment_id = a.assignment_id AND en.user_id = ? AND sub.is_late = 1
                     )
                     """);
                params.add(userId);
            } 
            else if ("PENDING".equalsIgnoreCase(status)) {
                sql.append("""
                     AND NOT EXISTS (
                         SELECT 1 FROM submissions sub 
                         JOIN enrollments en ON sub.enrollment_id = en.enrollment_id 
                         WHERE sub.assignment_id = a.assignment_id AND en.user_id = ?
                     )
                     """);
                params.add(userId);
            }
        }
    }
}
