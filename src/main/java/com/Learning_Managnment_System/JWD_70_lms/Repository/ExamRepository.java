package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamQuestionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ExamRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;

@Repository
public class ExamRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // 1. FIND ALL EXAMS
    // =========================================================

    public List<ExamBean> findAll() {

        String sql = """
            SELECT
                exam_id,
                course_id,
                batch_id,
                title,
                exam_type,
                duration_min,
                total_mark,
                pass_mark,
                start_at,
                end_at,
                max_attempts,
                shuffle_questions,
                status,
                created_by,
                created_at,
                updated_at
            FROM exams
            ORDER BY exam_id DESC
            """;

        return jdbcTemplate.query(
                sql,
                new ExamRowMapper()
        );
    }


    // =========================================================
    // 2. FIND EXAM BY ID
    // =========================================================

    public Optional<ExamBean> findById(Long examId) {

        String sql = """
            SELECT
                exam_id,
                course_id,
                batch_id,
                title,
                exam_type,
                duration_min,
                total_mark,
                pass_mark,
                start_at,
                end_at,
                max_attempts,
                shuffle_questions,
                status,
                created_by,
                created_at,
                updated_at
            FROM exams
            WHERE exam_id = ?
            """;

        List<ExamBean> list = jdbcTemplate.query(
                sql,
                new ExamRowMapper(),
                examId
        );

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }


    // =========================================================
    // 3. SAVE EXAM
    // =========================================================

    public Long save(ExamBean exam) {

        String sql = """
            INSERT INTO exams (
                course_id,
                batch_id,
                title,
                exam_type,
                duration_min,
                total_mark,
                pass_mark,
                start_at,
                end_at,
                max_attempts,
                shuffle_questions,
                status,
                created_by
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                        sql,
                        new String[] { "exam_id" }
                    );

            ps.setLong(1, exam.getCourseId());
            ps.setLong(2, exam.getBatchId());
            ps.setString(3, exam.getTitle());
            ps.setString(4, exam.getExamType());
            ps.setInt(5, exam.getDurationMin());
            ps.setBigDecimal(6, exam.getTotalMark());
            ps.setBigDecimal(7, exam.getPassMark());

            ps.setObject(8, exam.getStartAt());
            ps.setObject(9, exam.getEndAt());

            ps.setInt(10, exam.getMaxAttempts());
            ps.setBoolean(11, Boolean.TRUE.equals(exam.getShuffleQuestions()));
            ps.setString(12, exam.getStatus());
            ps.setLong(13, exam.getCreatedBy());

            return ps;

        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key == null) {
            throw new IllegalArgumentException(
                "Failed to generate exam ID"
            );
        }

        return key.longValue();
    }    // =========================================================
    // 4. UPDATE EXAM
    // =========================================================

    public int update(ExamBean exam) {

        String sql = """
            UPDATE exams
            SET
                course_id = ?,
                batch_id = ?,
                title = ?,
                exam_type = ?,
                duration_min = ?,
                total_mark = ?,
                pass_mark = ?,
                start_at = ?,
                end_at = ?,
                max_attempts = ?,
                shuffle_questions = ?,
                status = ?
            WHERE exam_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                exam.getCourseId(),
                exam.getBatchId(),
                exam.getTitle(),
                exam.getExamType(),
                exam.getDurationMin(),
                exam.getTotalMark(),
                exam.getPassMark(),
                exam.getStartAt(),
                exam.getEndAt(),
                exam.getMaxAttempts(),
                exam.getShuffleQuestions(),
                exam.getStatus(),
                exam.getExamId()
        );
    }


    // =========================================================
    // 5. DELETE EXAM
    // =========================================================

    public int deleteById(Long examId) {

        String sql = """
            DELETE FROM exams
            WHERE exam_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                examId
        );
    }


    // =========================================================
    // 6. GET QUESTIONS OF AN EXAM
    // =========================================================

    public List<ExamQuestionBean> findQuestionsByExamId(Long examId) {

        String sql = """
            SELECT
                exam_id,
                question_id,
                mark,
                sort_order
            FROM exam_questions
            WHERE exam_id = ?
            ORDER BY sort_order ASC
            """;

        return jdbcTemplate.query(
                sql,
                new ExamQuestionRowMapper(),
                examId
        );
    }


    // =========================================================
    // 7. ADD QUESTION TO EXAM
    // =========================================================

    public int addQuestionToExam(ExamQuestionBean examQuestion) {

        String sql = """
            INSERT INTO exam_questions (
                exam_id,
                question_id,
                mark,
                sort_order
            )
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                examQuestion.getExamId(),
                examQuestion.getQuestionId(),
                examQuestion.getMark(),
                examQuestion.getSortOrder()
        );
    }


    // =========================================================
    // 8. REMOVE ONE QUESTION FROM EXAM
    // =========================================================

    public int removeQuestionFromExam(
            Long examId,
            Long questionId) {

        String sql = """
            DELETE FROM exam_questions
            WHERE exam_id = ?
              AND question_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                examId,
                questionId
        );
    }


    // =========================================================
    // 9. REMOVE ALL QUESTIONS FROM EXAM
    // =========================================================

    public int removeAllQuestions(Long examId) {

        String sql = """
            DELETE FROM exam_questions
            WHERE exam_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                examId
        );
    }
    
    public boolean existsQuestionInExam(Long examId, Long questionId) {

        String sql = """
            SELECT COUNT(*)
            FROM exam_questions
            WHERE exam_id = ?
              AND question_id = ?
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            examId,
            questionId
        );

        return count != null && count > 0;
    }
    
    public Integer getNextSortOrder(Long examId) {

        String sql = """
            SELECT COALESCE(MAX(sort_order), 0) + 1
            FROM exam_questions
            WHERE exam_id = ?
            """;

        return jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            examId
        );
    }
}