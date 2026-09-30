package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

@Repository
public class QuestionRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==========================================
    // 1. Get All Questions
    // ==========================================
    public List<QuestionBean> getAllQuestions() {

        String sql = """
                SELECT question_id,
                       course_id,
                       question_text,
                       question_type,
                       default_mark,
                       explanation,
                       is_active,
                       created_by,
                       created_at,
                       updated_at
                FROM questions
                ORDER BY question_id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            QuestionBean question = new QuestionBean();

            question.setQuestionId(rs.getLong("question_id"));
            question.setCourseId(rs.getLong("course_id"));
            question.setQuestionText(rs.getString("question_text"));
            question.setQuestionType(rs.getString("question_type"));
            question.setDefaultMark(rs.getBigDecimal("default_mark"));
            question.setExplanation(rs.getString("explanation"));
            question.setIsActive(rs.getBoolean("is_active"));
            question.setCreatedBy(rs.getLong("created_by"));

            if (rs.getTimestamp("created_at") != null) {
                question.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

            if (rs.getTimestamp("updated_at") != null) {
                question.setUpdatedAt(
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );
            }

            return question;
        });
    }

    // ==========================================
    // 2. Get Question By ID
    // ==========================================
    public QuestionBean getQuestionById(Long questionId) {

        String sql = """
                SELECT question_id,
                       course_id,
                       question_text,
                       question_type,
                       default_mark,
                       explanation,
                       is_active,
                       created_by,
                       created_at,
                       updated_at
                FROM questions
                WHERE question_id = ?
                """;

        List<QuestionBean> list = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    QuestionBean question = new QuestionBean();

                    question.setQuestionId(rs.getLong("question_id"));
                    question.setCourseId(rs.getLong("course_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setQuestionType(rs.getString("question_type"));
                    question.setDefaultMark(rs.getBigDecimal("default_mark"));
                    question.setExplanation(rs.getString("explanation"));
                    question.setIsActive(rs.getBoolean("is_active"));
                    question.setCreatedBy(rs.getLong("created_by"));

                    if (rs.getTimestamp("created_at") != null) {
                        question.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp("updated_at") != null) {
                        question.setUpdatedAt(
                            rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                    }

                    return question;
                },
                questionId
        );

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    // ==========================================
    // 3. Save Question
    // ==========================================
    public Long saveQuestion(QuestionBean question) {

        String sql = """
                INSERT INTO questions
                (
                    course_id,
                    question_text,
                    question_type,
                    default_mark,
                    explanation,
                    is_active,
                    created_by
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, question.getCourseId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getQuestionType());
            ps.setBigDecimal(4, question.getDefaultMark());
            ps.setString(5, question.getExplanation());
            ps.setBoolean(6, question.getIsActive());
            ps.setLong(7, question.getCreatedBy());

            return ps;

        }, keyHolder);

        Long questionId = keyHolder.getKey().longValue();

        question.setQuestionId(questionId);

        return questionId;
    }

    public int updateQuestion(QuestionBean question) {

        String sql = """
                UPDATE questions
                SET course_id = ?,
                    question_text = ?,
                    question_type = ?,
                    default_mark = ?,
                    explanation = ?,
                    is_active = ?
                WHERE question_id = ?
                """;

        int result = jdbcTemplate.update(
                sql,
                question.getCourseId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getDefaultMark(),
                question.getExplanation(),
                question.getIsActive(),
                question.getQuestionId()
        );

        return result;
    }

    // ==========================================
    // 5. Delete Question
    // ==========================================
    public int deleteQuestion(Long questionId) {

        String sql = """
                DELETE FROM questions
                WHERE question_id = ?
                """;

        return jdbcTemplate.update(sql, questionId);
    }

    // ==========================================
    // 6. Get Options By Question ID
    // ==========================================
    public List<QuestionOptionBean> getOptionsByQuestionId(Long questionId) {

        String sql = """
                SELECT option_id,
                       question_id,
                       option_label,
                       option_text,
                       is_correct
                FROM question_options
                WHERE question_id = ?
                ORDER BY option_label
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    QuestionOptionBean option = new QuestionOptionBean();

                    option.setOptionId(rs.getLong("option_id"));
                    option.setQuestionId(rs.getLong("question_id"));
                    option.setOptionLabel(rs.getString("option_label"));
                    option.setOptionText(rs.getString("option_text"));
                    option.setIsCorrect(rs.getBoolean("is_correct"));

                    return option;
                },
                questionId
        );
    }

    // ==========================================
    // 7. Save Question Option
    // ==========================================
    public int saveOption(QuestionOptionBean option) {

        String sql = """
                INSERT INTO question_options
                (
                    question_id,
                    option_label,
                    option_text,
                    is_correct
                )
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                option.getQuestionId(),
                option.getOptionLabel(),
                option.getOptionText(),
                option.getIsCorrect()
        );
    }

    // ==========================================
    // 8. Delete Options By Question ID
    // ==========================================
    public int deleteOptionsByQuestionId(Long questionId) {

        String sql = """
                DELETE FROM question_options
                WHERE question_id = ?
                """;

        return jdbcTemplate.update(sql, questionId);
    }
}