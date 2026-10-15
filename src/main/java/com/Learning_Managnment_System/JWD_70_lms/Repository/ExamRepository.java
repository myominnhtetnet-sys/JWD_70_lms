package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamQuestionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ExamRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAvailableExams(String email) {
        String sql = "SELECT e.exam_id, e.title, e.exam_type, e.duration_min, e.total_mark " +
                     "FROM lms_db.exams e " +
                     "JOIN lms_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN lms_db.enrollments en ON b.batch_id = en.batch_id " +
                     "JOIN lms_db.users u ON en.user_id = u.user_id " +
                     "WHERE u.email = ? AND e.status = 'PUBLISHED' AND en.status = 'ACTIVE'";
        return jdbcTemplate.queryForList(sql, email);
    }

    public List<Map<String, Object>> findHistoricResults(String email) {
        String sql = "SELECT ea.attempt_id, e.title, e.exam_type, ea.attempt_no, " +
                     "ea.submitted_at, ea.score, e.total_mark, ea.result, ea.status " +
                     "FROM lms_db.exam_attempts ea " +
                     "JOIN lms_db.exams e ON ea.exam_id = e.exam_id " +
                     "JOIN lms_db.enrollments en ON ea.enrollment_id = en.enrollment_id " +
                     "JOIN lms_db.users u ON en.user_id = u.user_id " +
                     "WHERE u.email = ? AND ea.status IN ('SUBMITTED', 'GRADED') " +
                     "ORDER BY ea.submitted_at DESC";
        return jdbcTemplate.queryForList(sql, email);
    }

    public int findEnrollmentIdByEmail(String email) {
        String sql = "SELECT e.enrollment_id FROM lms_db.enrollments e JOIN lms_db.users u ON e.user_id = u.user_id WHERE u.email = ? AND e.status = 'ACTIVE' LIMIT 1";
        Integer num = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return num != null ? num.intValue() : 0;
    }

    public Map<String, Object> findExamById(int examId) {
        String sql = "SELECT title, duration_min, total_mark, max_attempts FROM lms_db.exams WHERE exam_id = ? AND status = 'PUBLISHED'";
        return jdbcTemplate.queryForMap(sql, examId);
    }

    public List<Map<String, Object>> findActiveProgressAttempt(int examId, int enrollmentId) {
        String sql = "SELECT attempt_id, attempt_no FROM lms_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ? AND status = 'IN_PROGRESS' LIMIT 1";
        return jdbcTemplate.queryForList(sql, examId, enrollmentId);
    }

    public int countTotalAttempts(int examId, int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM lms_db.exam_attempts WHERE exam_id = ? AND enrollment_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, examId, enrollmentId);
        return count != null ? count.intValue() : 0;
    }

    public List<Integer> findQuestionPoolIds(int examId) {
        String sql = "SELECT question_id FROM lms_db.exam_questions WHERE exam_id = ? ORDER BY sort_order ASC";
        return jdbcTemplate.queryForList(sql, Integer.class, examId);
    }

    public int findFirstValidQuestionId(int examId) {
        String sql = "SELECT question_id FROM lms_db.exam_questions WHERE exam_id = ? ORDER BY sort_order ASC LIMIT 1";
        List<Integer> ids = jdbcTemplate.queryForList(sql, Integer.class, examId);
        return !ids.isEmpty() ? ids.get(0) : 1;
    }

    public ExamQuestionBean findQuestionMetadata(int questionId) {
        String sql = "SELECT question_id, question_text, question_type, points FROM lms_db.exam_questions WHERE question_id = ? LIMIT 1";
        ExamQuestionBean question = jdbcTemplate.queryForObject(sql, new ExamQuestionRowMapper(), questionId);
        
        if (question != null && "MCQ".equalsIgnoreCase(question.getQuestionType())) {
            String optionsSql = "SELECT option_id AS optionId, option_text AS optionText FROM lms_db.question_options WHERE question_id = ?";
            List<QuestionOptionBean> options = jdbcTemplate.query(optionsSql, new BeanPropertyRowMapper<>(QuestionOptionBean.class), questionId);
            question.setOptions(options);
        }
        return question;
    }

    public void forceTerminateAttempt(int attemptId) {
        String sql = "UPDATE lms_db.exam_attempts SET status = 'SUBMITTED', started_at = started_at WHERE attempt_id = ?";
        jdbcTemplate.update(sql, attemptId);
    }

    public void insertNewAttempt(int examId, int enrollmentId, int attemptNo) {
        String sql = "INSERT INTO lms_db.exam_attempts (exam_id, enrollment_id, attempt_no, started_at, status) VALUES (?, ?, ?, NOW(), 'IN_PROGRESS')";
        jdbcTemplate.update(sql, examId, enrollmentId, attemptNo);
    }

    public int getLastInsertId() {
        Number lastId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Number.class);
        return lastId != null ? lastId.intValue() : 0;
    }

    public void saveOrUpdateAnswer(int attemptId, int questionId, String text) {
        String sql = "INSERT INTO lms_db.attempt_answers (attempt_id, question_id, answer_text, is_correct, mark_awarded) VALUES (?, ?, ?, NULL, 0.0) ON DUPLICATE KEY UPDATE answer_text = ?";
        jdbcTemplate.update(sql, attemptId, questionId, text, text);
    }

    public void finalizeAttemptSubmission(int attemptId) {
        String sql = "UPDATE lms_db.exam_attempts SET submitted_at = NOW(), score = 0.0, result = 'PENDING', status = 'SUBMITTED' WHERE attempt_id = ?";
        jdbcTemplate.update(sql, attemptId);
    }

    public Map<String, Object> findAttemptResultSummary(int attemptId) {
        String sql = "SELECT ea.score, ea.result, ea.status, e.title, e.total_mark, e.pass_mark " +
                     "FROM lms_db.exam_attempts ea " +
                     "JOIN lms_db.exams e ON ea.exam_id = e.exam_id " +
                     "WHERE ea.attempt_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, attemptId);
        } catch (Exception e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("title", "Assessment Assignment");
            fallback.put("score", 0.0);
            fallback.put("total_mark", 100.0);
            fallback.put("pass_mark", 50.0);
            fallback.put("status", "SUBMITTED");
            fallback.put("result", "PENDING");
            return fallback;
        }
    }
}
