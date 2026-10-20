package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.ArrayList;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                WHERE u.email = ? ORDER BY ea.submitted_at DESC
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
        String sql = "SELECT * FROM learning_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ? AND status = 'IN_PROGRESS'";
        return jdbcTemplate.query(sql, new ExamAttemptRowMapper(), examId, enrollmentId);
    }

    @Transactional
    public void updateAttemptToTerminated(int attemptId) {
        String sql = "UPDATE learning_db.exam_attempts SET status = 'EXPIRED', result = 'FAILED', submitted_at = NOW() WHERE attempt_id = ?";
        jdbcTemplate.update(sql, attemptId);
    }

    @Transactional(readOnly = true)
    public Integer countPastAttempts(int examId, int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM learning_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ? AND status IN ('SUBMITTED', 'GRADED', 'EXPIRED')";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), examId, enrollmentId);
    }

    public List<Integer> findQuestionSequence(int examId) {
        String sql = "SELECT question_id FROM learning_db.exam_questions WHERE exam_id = ? ORDER BY sort_order ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("question_id"), examId);
    }

    @Transactional
    public int insertNewAttemptSession(int examId, int enrollmentId, int attemptNo) {
        String sql = "INSERT INTO learning_db.exam_attempts (exam_id, enrollment_id, attempt_no, status, result, started_at) VALUES (?, ?, ?, 'IN_PROGRESS', 'PENDING', NOW())";
        jdbcTemplate.update(sql, examId, enrollmentId, attemptNo);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", (rs, rowNum) -> rs.getInt(1));
    }

    public List<ExamQuestionBean> findQuestionsBySequence(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Clean out any malicious character inputs to protect from SQL injection risks
        String sanitizedSequence = sequence.replaceAll("[^0-9,]", "");
        if (sanitizedSequence.isEmpty()) {
            return new ArrayList<>();
        }

        // Main Query: Joins the relational structure using standard aliases
        String sql = "SELECT " +
                     "    q.question_id AS questionId, " +
                     "    q.question_text AS questionText, " +
                     "    q.question_type AS questionType, " +
                     "    eq.mark AS questionMark " +
                     "FROM learning_db.exam_questions eq " +
                     "JOIN learning_db.questions q ON eq.question_id = q.question_id " +
                     "WHERE eq.question_id IN (" + sanitizedSequence + ") " +
                     "ORDER BY FIELD(eq.question_id, " + sanitizedSequence + ")";

        List<ExamQuestionBean> questions;
        try {
            questions = jdbcTemplate.query(sql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(ExamQuestionBean.class));
        } catch (Exception e) {
            // 🟢 FIXED FALLBACK SQL: Drops complex decimal conversions to prevent type-mapping crashes
            String fallbackSql = "SELECT " +
                                 "    question_id AS questionId, " +
                                 "    question_text AS questionText, " +
                                 "    question_type AS questionType " +
                                 "FROM learning_db.questions " +
                                 "WHERE question_id IN (" + sanitizedSequence + ") " +
                                 "ORDER BY FIELD(question_id, " + sanitizedSequence + ")";
            
            questions = jdbcTemplate.query(fallbackSql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(ExamQuestionBean.class));
        }

        // Hydration Cycle: Append child options lists arrays for each question entry dynamically
        for (ExamQuestionBean q : questions) {
            List<QuestionOptionBean> options = new ArrayList<>();
            
            try {
                String optionsSql = "SELECT option_id AS optionId, question_id AS questionId, option_label AS optionLabel, option_text AS optionText FROM learning_db.question_options WHERE question_id = ?";
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

    @Transactional
    public void saveSubmissionResponse(int examId, int attemptId, String answerText) {
        String sql = "UPDATE learning_db.exam_attempts SET remark = ?, status = 'SUBMITTED', submitted_at = NOW() WHERE attempt_id = ?";
        jdbcTemplate.update(sql, answerText, attemptId);
    }
}
