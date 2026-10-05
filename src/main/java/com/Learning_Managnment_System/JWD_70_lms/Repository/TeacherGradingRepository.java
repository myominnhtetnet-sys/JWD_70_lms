
package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.SubmissionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Repository
public class TeacherGradingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SubmissionRowMapper submissionRowMapper;

    public TeacherGradingRepository(
            JdbcTemplate jdbcTemplate,
            SubmissionRowMapper submissionRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.submissionRowMapper = submissionRowMapper;
    }

    public List<SubmissionBean> findAllSubmissions() {

        String sql = """
                SELECT
                    s.*,
                    s.mark AS score,
                    a.title AS assignment_title,
                    a.total_mark AS total_mark,
                    u.full_name AS student_name

                FROM submissions s

                JOIN assignments a
                    ON s.assignment_id = a.assignment_id

                JOIN enrollments e
                    ON s.enrollment_id = e.enrollment_id

                JOIN users u
                    ON e.user_id = u.user_id

                ORDER BY s.submission_id DESC
                """;

        return jdbcTemplate.query(sql, submissionRowMapper);
    }
    
    public int gradeSubmission(Integer submissionId,
            BigDecimal score,String feedback,Integer gradedBy) {

        String sql = """
                UPDATE submissions
                SET mark = ?,
                    feedback = ?,
                    graded_by = ?,
                    graded_at = NOW(),
                    status = 'GRADED',
                    updated_at = NOW()
                WHERE submission_id = ?
                """;

        return jdbcTemplate.update(
                sql,score,feedback,
                gradedBy,submissionId);
    }
    
    public Optional<SubmissionBean> findSubmissionById(Integer submissionId) {

        String sql = """
                SELECT
                    s.*,
                    s.mark AS score,
                    a.title AS assignment_title,
                    a.total_mark AS total_mark,
                    u.full_name AS student_name

                FROM submissions s

                JOIN assignments a
                    ON s.assignment_id = a.assignment_id

                JOIN enrollments e
                    ON s.enrollment_id = e.enrollment_id

                JOIN users u
                    ON e.user_id = u.user_id

                WHERE s.submission_id = ?
                """;

        List<SubmissionBean> result = jdbcTemplate.query(
                sql,submissionRowMapper,
                submissionId);

        return result.stream().findFirst();
    }
}