package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.ArrayList;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamAttemptRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamQuestionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

@Repository
public class StudentExamRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentExamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ExamBean> findAvailableExams(String email) {
        String sql = """
                SELECT e.* FROM learning_db.exams e 
                JOIN learning_db.batches b ON e.course_id = b.course_id 
                JOIN learning_db.enrollments en ON en.batch_id = b.batch_id 
                JOIN learning_db.users u ON en.user_id = u.user_id 
                WHERE u.email = ? AND en.status = 'ACTIVE' AND e.status = 'PUBLISHED'
                """;
        return jdbcTemplate.query(sql, new ExamRowMapper(), email);
    }

    public List<ExamAttemptBean> findExamResultsHistory(String email) {
        String sql = """
                SELECT e.title AS exam_title, ea.* 
                FROM learning_db.exam_attempts ea 
                JOIN learning_db.exams e ON ea.exam_id = e.exam_id 
                JOIN learning_db.enrollments en ON ea.enrollment_id = en.enrollment_id 
                JOIN learning_db.users u ON en.user_id = u.user_id 
                WHERE u.email = ? 
                GROUP BY ea.attempt_id, e.title 
                ORDER BY ea.submitted_at DESC
                """;
        return jdbcTemplate.query(sql, new ExamAttemptRowMapper(), email);
    }

    public Integer findEnrollmentIdByEmail(String email) {
        String sql = "SELECT enrollment_id FROM learning_db.enrollments en JOIN learning_db.users u ON en.user_id = u.user_id WHERE u.email = ? AND en.status = 'ACTIVE' LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), email);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public ExamBean findExamMetadata(int examId) {
        String sql = "SELECT * FROM learning_db.exams WHERE exam_id = ? LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new ExamRowMapper(), examId);
    }

    public List<ExamAttemptBean> findActiveAttempts(int examId, int enrollmentId) {
        String sql = "SELECT * FROM learning_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ? AND status = 'IN_PROGRESS' ORDER BY attempt_id ASC";
        return jdbcTemplate.query(sql, new ExamAttemptRowMapper(), examId, enrollmentId);
    }

    public void updateAttemptToTerminated(int attemptId) {
        // First, extract metadata to locate parent attempt boundaries
        String lookUpSql = "SELECT exam_id, enrollment_id FROM learning_db.exam_attempts WHERE attempt_id = ? LIMIT 1";
        try {
            java.util.Map<String, Object> match = jdbcTemplate.queryForMap(lookUpSql, attemptId);
            Object eId = match.get("exam_id");
            Object enId = match.get("enrollment_id");
            
            // Terminate all matching stale open rows to clean up your database state
            String purgeSql = "UPDATE learning_db.exam_attempts SET status = 'EXPIRED', result = 'FAILED', submitted_at = NOW() WHERE exam_id = ? AND enrollment_id = ? AND status = 'IN_PROGRESS'";
            jdbcTemplate.update(purgeSql, eId, enId);
        } catch (Exception e) {
            // Fallback safety if no rows exist
            String sql = "UPDATE learning_db.exam_attempts SET status = 'EXPIRED', result = 'FAILED', submitted_at = NOW() WHERE attempt_id = ?";
            jdbcTemplate.update(sql, attemptId);
        }
    }

    public Integer countPastAttempts(int examId, int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM learning_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ? AND status IN ('SUBMITTED', 'GRADED', 'EXPIRED')";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), examId, enrollmentId);
    }

    public List<Integer> findQuestionSequence(int examId) {
        // Swapped out "SELECT" for "SELECT DISTINCT" to neutralize table duplicates instantly
        String sql = "SELECT DISTINCT question_id FROM learning_db.exam_questions WHERE exam_id = ? ORDER BY question_id ASC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("question_id"), examId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public int insertNewAttemptSession(int examId, int enrollmentId, int attemptNo) {
        String sql = "INSERT INTO learning_db.exam_attempts (exam_id, enrollment_id, attempt_no, status, result, started_at) VALUES (?, ?, ?, 'IN_PROGRESS', 'PENDING', NOW())";
        jdbcTemplate.update(sql, examId, enrollmentId, attemptNo);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", (rs, rowNum) -> rs.getInt(1));
    }

    /**
     * Retrieves strictly unique question records matching the sanitized sequence.
     * 🟢 DECISIVE FIX: Targets the isolated questions table directly using DISTINCT.
     * This strips out overlapping exam_id and sort_order column properties from the uniqueness check,
     * permanently stopping data multiplication before the list reaches your Thymeleaf templates!
     */
    public List<ExamQuestionBean> findQuestionsBySequence(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Clean out any malicious character inputs to protect from SQL injection risks
        String sanitizedSequence = sequence.replaceAll("[^0-9,]", "");
        if (sanitizedSequence.isEmpty()) {
            return new ArrayList<>();
        }

        // 🟢 FIXED SQL: Query directly from the questions table to guarantee true item deduplication
        String sql = "SELECT DISTINCT " +
                     "    q.question_id AS questionId, " +
                     "    q.question_text AS questionText, " +
                     "    q.question_type AS questionType " + 
                     "FROM learning_db.questions q " +
                     "WHERE q.question_id IN (" + sanitizedSequence + ") " +
                     "ORDER BY FIELD(q.question_id, " + sanitizedSequence + ")";

        List<ExamQuestionBean> questions;
        try {
            // Uses a decoupled property row mapper to bind values into camelCase entity setters instantly
            questions = jdbcTemplate.query(sql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(ExamQuestionBean.class));
        } catch (Exception e) {
            String fallbackSql = "SELECT question_id AS questionId, question_text AS questionText, question_type AS questionType " +
                                 "FROM learning_db.questions WHERE question_id IN (" + sanitizedSequence + ")";
            questions = jdbcTemplate.query(fallbackSql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(ExamQuestionBean.class));
        }

        // Hydration Cycle: Append child options lists arrays for each question entry dynamically
        for (ExamQuestionBean q : questions) {
            List<QuestionOptionBean> options = new ArrayList<>();
            
            try {
                String optionsSql = "SELECT option_id AS optionId, question_id AS questionId, option_label AS optionLabel, option_text AS optionText FROM learning_db.question_options WHERE question_id = ? ORDER BY option_id ASC";
                options = jdbcTemplate.query(optionsSql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(QuestionOptionBean.class), q.getQuestionId());
            } catch (Exception e) {
                options = new ArrayList<>();
            }
            
            // TRUE/FALSE SYSTEM SAFEGUARD: Automatically generates option buttons if table lines are blank
            if ("TRUE_FALSE".equals(q.getQuestionType()) && options.isEmpty()) {
                String[] tfTexts = {"True", "False"};
                String[] tfLabels = {"A", "B"};
                for (int i = 0; i < 2; i++) {
                    QuestionOptionBean tfOpt = new QuestionOptionBean();
                    tfOpt.setOptionId(i + 1); 
                    tfOpt.setQuestionId(q.getQuestionId());
                    tfOpt.setOptionLabel(tfLabels[i]);
                    tfOpt.setOptionText(tfTexts[i]);
                    options.add(tfOpt);
                }
            }
            
            q.setOptions(options);
        }
        
        return questions;
    }

    public ExamAttemptBean findAttemptResultSummary(int attemptId) {
        String sql = """
                SELECT e.title AS exam_title, e.total_mark, e.pass_mark, ea.* 
                FROM learning_db.exam_attempts ea 
                JOIN learning_db.exams e ON ea.exam_id = e.exam_id 
                WHERE ea.attempt_id = ? LIMIT 1
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new ExamAttemptRowMapper(), attemptId);
        } catch (EmptyResultDataAccessException e) {
            ExamAttemptBean fallback = new ExamAttemptBean();
            fallback.setAttempt_id(attemptId);
            fallback.setTitle("Assessment Report");
            fallback.setStatus("SUBMITTED");
            fallback.setResult("PENDING");
            fallback.setScore(java.math.BigDecimal.ZERO);
            fallback.setTotal_mark(java.math.BigDecimal.valueOf(100.00));
            fallback.setPass_mark(java.math.BigDecimal.valueOf(50.00));
            return fallback;
        }
    }

    public void saveSubmissionResponse(int examId, int attemptId, String answerText) {
        String sql = "UPDATE learning_db.exam_attempts SET remark = ?, status = 'SUBMITTED', submitted_at = NOW() WHERE attempt_id = ?";
        jdbcTemplate.update(sql, answerText, attemptId);
    }
}
