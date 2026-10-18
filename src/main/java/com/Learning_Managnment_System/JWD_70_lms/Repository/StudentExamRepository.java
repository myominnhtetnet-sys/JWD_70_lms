
package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamAttemptRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamQuestionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentExamBean;

@Repository
public class StudentExamRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentExamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Student Exam List
    public List<StudentExamBean> findAvailableExams(String email) {

        String sql = """
            SELECT DISTINCT
                e.exam_id,
                e.course_id,
                e.batch_id,
                e.title,
                e.exam_type,
                e.duration_min,
                e.total_mark,
                e.pass_mark,
                e.max_attempts
            FROM learning_db.exams e
            INNER JOIN learning_db.batches b
                ON e.batch_id = b.batch_id
            INNER JOIN learning_db.enrollments en
                ON en.batch_id = b.batch_id
            INNER JOIN learning_db.users u
                ON en.user_id = u.user_id
            WHERE u.email = ?
              AND en.status = 'ACTIVE'
              AND e.status = 'PUBLISHED'
            ORDER BY e.exam_id DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StudentExamBean exam = new StudentExamBean();

            exam.setExam_id(rs.getInt("exam_id"));
            exam.setCourse_id(rs.getInt("course_id"));
            exam.setBatch_id(rs.getInt("batch_id"));
            exam.setTitle(rs.getString("title"));
            exam.setExam_type(rs.getString("exam_type"));
            exam.setDuration_min(rs.getShort("duration_min"));
            exam.setTotal_mark(rs.getBigDecimal("total_mark"));
            exam.setPass_mark(rs.getBigDecimal("pass_mark"));
            exam.setMax_attempts(rs.getByte("max_attempts"));

            return exam;
        }, email);
    }

    // 2. Student Exam Results History
    public List<ExamAttemptBean> findExamResultsHistory(String email) {

        String sql = """
            SELECT
                ea.*,
                e.title AS exam_title,
                e.total_mark,
                e.pass_mark
            FROM learning_db.exam_attempts ea
            INNER JOIN learning_db.exams e
                ON ea.exam_id = e.exam_id
            INNER JOIN learning_db.enrollments en
                ON ea.enrollment_id = en.enrollment_id
            INNER JOIN learning_db.users u
                ON en.user_id = u.user_id
            WHERE u.email = ?
            ORDER BY ea.attempt_id DESC
            """;

        return jdbcTemplate.query(
            sql, new ExamAttemptRowMapper(), email);
    }

    // 3. Find Student Enrollment ID
    public Integer findEnrollmentIdByEmail(String email) {

        String sql = """
            SELECT en.enrollment_id
            FROM learning_db.enrollments en
            INNER JOIN learning_db.users u
                ON en.user_id = u.user_id
            WHERE u.email = ?
              AND en.status = 'ACTIVE'
            ORDER BY en.enrollment_id DESC
            LIMIT 1
            """;

        try {
            return jdbcTemplate.queryForObject(
                sql, Integer.class, email);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 4. Get Student Exam Metadata
    public StudentExamBean findStudentExamMetadata(int examId) {

        String sql = """
            SELECT
                exam_id, course_id, batch_id, title,
                exam_type, duration_min, total_mark,
                pass_mark, max_attempts
            FROM learning_db.exams
            WHERE exam_id = ?
              AND status = 'PUBLISHED'
            LIMIT 1
            """;

        List<StudentExamBean> exams = jdbcTemplate.query(
            sql, (rs, rowNum) -> {
                StudentExamBean exam = new StudentExamBean();

                exam.setExam_id(rs.getInt("exam_id"));
                exam.setCourse_id(rs.getInt("course_id"));
                exam.setBatch_id(rs.getInt("batch_id"));
                exam.setTitle(rs.getString("title"));
                exam.setExam_type(rs.getString("exam_type"));
                exam.setDuration_min(rs.getShort("duration_min"));
                exam.setTotal_mark(rs.getBigDecimal("total_mark"));
                exam.setPass_mark(rs.getBigDecimal("pass_mark"));
                exam.setMax_attempts(rs.getByte("max_attempts"));

                return exam;
            },
            examId
        );

        return exams.isEmpty() ? null : exams.get(0);
    }

    // 5. Check Active Attempts
    public List<ExamAttemptBean> findActiveAttempts(
            int examId, int enrollmentId) {

        String sql = """
            SELECT *
            FROM learning_db.exam_attempts
            WHERE exam_id = ?
              AND enrollment_id = ?
              AND status = 'IN_PROGRESS'
            """;

        return jdbcTemplate.query(
            sql, new ExamAttemptRowMapper(), examId, enrollmentId);
    }

    // 6. Terminate an Active Attempt
    @Transactional
    public void updateAttemptToTerminated(int attemptId) {

        String sql = """
            UPDATE learning_db.exam_attempts
            SET status = 'EXPIRED',
                result = 'FAILED',
                submitted_at = NOW()
            WHERE attempt_id = ?
              AND status = 'IN_PROGRESS'
            """;

        jdbcTemplate.update(sql, attemptId);
    }

    // 7. Count Previous Attempts
    public Integer countPastAttempts(int examId, int enrollmentId) {

        String sql = """
            SELECT COUNT(*)
            FROM learning_db.exam_attempts
            WHERE exam_id = ?
              AND enrollment_id = ?
              AND status IN ('SUBMITTED', 'GRADED', 'EXPIRED')
            """;

        return jdbcTemplate.queryForObject(
            sql, Integer.class, examId, enrollmentId);
    }

    // 8. Get Question IDs in Exam Order
    public List<Integer> findQuestionSequence(int examId) {

        String sql = """
            SELECT question_id
            FROM learning_db.exam_questions
            WHERE exam_id = ?
            ORDER BY sort_order ASC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> rs.getInt("question_id"),
            examId
        );
    }

    // 9. Create New Attempt
    @Transactional
    public int insertNewAttemptSession(
            int examId, int enrollmentId, int attemptNo) {

        String sql = """
            INSERT INTO learning_db.exam_attempts
                (exam_id, enrollment_id, attempt_no,
                 status, result, started_at)
            VALUES (?, ?, ?, 'IN_PROGRESS', 'PENDING', NOW())
            """;

        jdbcTemplate.update(sql, examId, enrollmentId, attemptNo);

        Integer attemptId = jdbcTemplate.queryForObject(
            "SELECT LAST_INSERT_ID()", Integer.class);

        if (attemptId == null) {
            throw new IllegalStateException(
                "Could not create exam attempt.");
        }

        return attemptId;
    }

    // 10. Get Exam Questions and Options
    public List<ExamQuestionBean> findQuestionsBySequence(
            String queryIds) {

        if (queryIds == null || queryIds.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();

        try {
            for (String value : queryIds.split(",")) {
                int id = Integer.parseInt(value.trim());

                if (id <= 0) {
                    return Collections.emptyList();
                }

                ids.add(id);
            }
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(
            ",", Collections.nCopies(ids.size(), "?"));

        String sql = """
            SELECT
                eq.exam_id,
                eq.question_id,
                q.question_text,
                q.question_type,
                eq.mark,
                eq.sort_order
            FROM learning_db.exam_questions eq
            INNER JOIN learning_db.questions q
                ON q.question_id = eq.question_id
            WHERE eq.question_id IN (%s)
            ORDER BY eq.sort_order ASC
            """.formatted(placeholders);

        List<ExamQuestionBean> questions = jdbcTemplate.query(
            sql,
            new ExamQuestionRowMapper(),
            ids.toArray()
        );

        for (ExamQuestionBean question : questions) {

            String optionsSql = """
                SELECT option_id, question_id,
                       option_label, option_text
                FROM learning_db.question_options
                WHERE question_id = ?
                ORDER BY option_id ASC
                """;

            List<QuestionOptionBean> options = jdbcTemplate.query(
                optionsSql,
                (rs, rowNum) -> {
                    QuestionOptionBean option =
                        new QuestionOptionBean();

                    option.setOptionId(rs.getInt("option_id"));
                    option.setQuestionId(rs.getInt("question_id"));
                    option.setOptionLabel(
                        rs.getString("option_label"));
                    option.setOptionText(
                        rs.getString("option_text"));

                    return option;
                },
                question.getQuestionId()
            );

            // Provide True/False choices if no options are stored.
            if ("TRUE_FALSE".equalsIgnoreCase(
                    question.getQuestionType())
                    && options.isEmpty()) {

                QuestionOptionBean trueOption =
                    new QuestionOptionBean();
                trueOption.setOptionId(1);
                trueOption.setQuestionId(
                	    Math.toIntExact(question.getQuestionId())
                	);                trueOption.setOptionLabel("A");
                trueOption.setOptionText("True");
                options.add(trueOption);

                QuestionOptionBean falseOption =
                    new QuestionOptionBean();
                falseOption.setOptionId(2);
                falseOption.setQuestionId(
                	    Math.toIntExact(question.getQuestionId())
                	);	                falseOption.setOptionLabel("B");
                falseOption.setOptionText("False");
                options.add(falseOption);
            }

            question.setOptions(options);
        }   
        

        return questions;
    }

    // 11. Submit Exam Attempt
    @Transactional
    public void saveSubmissionResponse(
            int examId, int attemptId, String answerText) {

        String sql = """
            UPDATE learning_db.exam_attempts
            SET remark = ?,
                status = 'SUBMITTED',
                submitted_at = NOW()
            WHERE attempt_id = ?
              AND exam_id = ?
              AND status = 'IN_PROGRESS'
            """;

        int updated = jdbcTemplate.update(
            sql, answerText, attemptId, examId);

        if (updated != 1) {
            throw new IllegalStateException(
                "Exam attempt was not found or is no longer active.");
        }
    }

    // 12. Get Attempt Result Summary
    public ExamAttemptBean findAttemptResultSummary(int attemptId) {

        String sql = """
            SELECT
                ea.*,
                e.title AS exam_title,
                e.title,
                e.total_mark,
                e.pass_mark
            FROM learning_db.exam_attempts ea
            INNER JOIN learning_db.exams e
                ON ea.exam_id = e.exam_id
            WHERE ea.attempt_id = ?
            LIMIT 1
            """;

        List<ExamAttemptBean> results = jdbcTemplate.query(
            sql, new ExamAttemptRowMapper(), attemptId);

        return results.isEmpty() ? null : results.get(0);
    }
    
    
}
