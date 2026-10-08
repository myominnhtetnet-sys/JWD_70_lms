package com.Learning_Managnment_System.JWD_70_lms.Repository;

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
                
        // 🟢 PASS THE userId PARAMETER HERE SO SPRING CAN SAFELY SUBSTITUTE THE '?' PLACEHOLDER
        return jdbcTemplate.query(sql, assignmentRowMapper, userId);
    }


    public Optional<AssignmentBean> findAssignmentById(Integer assignmentId) {
        String sql = "SELECT * FROM assignments WHERE assignment_id = ? AND status = 'PUBLISHED'";
        List<AssignmentBean> result = jdbcTemplate.query(sql, assignmentRowMapper, assignmentId);
        return result.stream().findFirst();
    }

 // 🟢 FIXED: Added users JOIN to provide 'student_name' to the RowMapper
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

    // 🟢 FIXED: Updated late assignments query method with the same column allocations
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
    
    // 🟢 ADD THIS: Count total active assignments matching the student's batches
    public int getTotalAssignmentsCount(int userId) {
        String sql = """
                SELECT COUNT(*)
                FROM assignments a
                JOIN batches b ON a.batch_id = b.batch_id
                JOIN enrollments e ON b.batch_id = e.batch_id
                WHERE e.user_id = ? AND a.status = 'PUBLISHED' AND e.status = 'ACTIVE'
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    // 🟢 ADD THIS: Fetch chunked page cards using dynamic OFFSET calculations
    public List<AssignmentBean> findPaginatedAssignmentsByStudentId(int userId, int page, int pageSize) {
        int offset = page * pageSize; // Computes standard mathematical page slicing offsets
        
        String sql = """
                SELECT a.*, b.batch_code AS batch_code, b.title AS batch_title, l.title AS lesson_title
                FROM assignments a
                JOIN batches b ON a.batch_id = b.batch_id
                JOIN enrollments e ON b.batch_id = e.batch_id
                LEFT JOIN lessons l ON a.lesson_id = l.lesson_id
                WHERE e.user_id = ? AND a.status = 'PUBLISHED' AND e.status = 'ACTIVE'
                ORDER BY a.due_at ASC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, assignmentRowMapper, userId, pageSize, offset);
    }



}
