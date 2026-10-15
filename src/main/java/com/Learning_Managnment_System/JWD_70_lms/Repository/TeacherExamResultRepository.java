package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherExamResultRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeacherExamResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> findUserIdByEmail(String email) {

        List<Long> ids = jdbcTemplate.query(
                "SELECT user_id FROM users WHERE email = ?",
                (rs, rowNum) -> rs.getLong("user_id"),
                email);

        return ids.stream().findFirst();
    }

    public List<Map<String, Object>> findResults(Long teacherId) {

        String sql = """
                SELECT
                    ea.attempt_id AS attemptId,
                    ea.exam_id AS examId,
                    ea.attempt_no AS attemptNo,
                    ea.submitted_at AS submittedAt,
                    ea.score AS score,
                    ea.result AS result,
                    ea.status AS status,
                    ex.title AS examTitle,
                    ex.total_mark AS totalMark,
                    u.full_name AS studentName
                FROM exam_attempts ea
                JOIN exams ex
                    ON ex.exam_id = ea.exam_id
                JOIN enrollments en
                    ON en.enrollment_id = ea.enrollment_id
                JOIN users u
                    ON u.user_id = en.user_id
                WHERE ex.created_by = ?
                  AND ea.status IN ('SUBMITTED', 'GRADED')
                ORDER BY ea.attempt_id DESC
                """;

        return jdbcTemplate.queryForList(sql, teacherId);
    }

    public Optional<Map<String, Object>> findAttempt(
            Long attemptId,
            Long teacherId,
            boolean lock) {

        String sql = """
                SELECT
                    ea.attempt_id AS attemptId,
                    ea.exam_id AS examId,
                    ea.attempt_no AS attemptNo,
                    ea.submitted_at AS submittedAt,
                    ea.score AS score,
                    ea.result AS result,
                    ea.status AS status,
                    ea.remark AS remark,
                    ex.title AS examTitle,
                    ex.total_mark AS totalMark,
                    ex.pass_mark AS passMark,
                    u.full_name AS studentName
                FROM exam_attempts ea
                JOIN exams ex
                    ON ex.exam_id = ea.exam_id
                JOIN enrollments en
                    ON en.enrollment_id = ea.enrollment_id
                JOIN users u
                    ON u.user_id = en.user_id
                WHERE ea.attempt_id = ?
                  AND ex.created_by = ?
                """;

        if (lock) {
            sql += " FOR UPDATE";
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                sql, attemptId, teacherId);

        return rows.stream().findFirst();
    }

    public List<Map<String, Object>> findAnswers(
            Long attemptId,
            Long examId) {

        String sql = """
                SELECT
                    eq.question_id AS questionId,
                    eq.sort_order AS sortOrder,
                    eq.mark AS maxMark,
                    q.question_text AS questionText,
                    q.question_type AS questionType,
                    q.explanation AS explanation,
                    aa.answer_text AS answerText,
                    aa.mark_awarded AS markAwarded,
                    aa.selected_option_id AS selectedOptionId,
                    qo.option_text AS selectedOptionText,
                    qo.is_correct AS optionCorrect
                FROM exam_questions eq
                JOIN questions q
                    ON q.question_id = eq.question_id
                LEFT JOIN attempt_answers aa
                    ON aa.question_id = eq.question_id
                   AND aa.attempt_id = ?
                LEFT JOIN question_options qo
                    ON qo.option_id = aa.selected_option_id
                   AND qo.question_id = eq.question_id
                WHERE eq.exam_id = ?
                ORDER BY eq.sort_order, eq.question_id
                """;

        return jdbcTemplate.queryForList(sql, attemptId, examId);
    }

    public void saveAnswerMark(
            Long attemptId,
            Long questionId,
            BigDecimal mark,
            Boolean correct) {

        /*
         * Missing answer rows are also handled.
         * Existing student answer_text and selected_option_id are preserved.
         */
        String sql = """
                INSERT INTO attempt_answers
                    (attempt_id, question_id, mark_awarded, is_correct)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    mark_awarded = ?,
                    is_correct = ?
                """;

        jdbcTemplate.update(
                sql,
                attemptId,
                questionId,
                mark,
                correct,
                mark,
                correct);
    }

    public int completeGrading(
            Long attemptId,
            BigDecimal score,
            String result,
            String remark) {

        String sql = """
                UPDATE exam_attempts
                SET score = ?,
                    result = ?,
                    status = 'GRADED',
                    remark = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE attempt_id = ?
                  AND status IN ('SUBMITTED', 'GRADED')
                """;

        return jdbcTemplate.update(
                sql, score, result, remark, attemptId);
    }
}

