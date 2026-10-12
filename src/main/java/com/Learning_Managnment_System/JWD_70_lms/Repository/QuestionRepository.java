package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.QuestionOptionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.QuestionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

@Repository
public class QuestionRepository {

	private final JdbcTemplate jdbcTemplate;
	private final QuestionRowMapper questionRowMapper = new QuestionRowMapper();
	private final QuestionOptionRowMapper questionOptionRowMapper = new QuestionOptionRowMapper();

	public QuestionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<QuestionBean> getAllQuestions() {
		String sql = """
				SELECT
				    question_id,
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

		return jdbcTemplate.query(sql, questionRowMapper);
	}

	public QuestionBean getQuestionById(Long questionId) {
		String sql = """
				SELECT
				    question_id,
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

		List<QuestionBean> list = jdbcTemplate.query(sql, questionRowMapper, questionId);

		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

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

			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setLong(1, question.getCourseId());
			ps.setString(2, question.getQuestionText());
			ps.setString(3, question.getQuestionType());
			ps.setBigDecimal(4, question.getDefaultMark());
			ps.setString(5, question.getExplanation());
			ps.setBoolean(6, question.getIsActive());
			ps.setLong(7, question.getCreatedBy());

			return ps;
		}, keyHolder);

		Long generatedId = keyHolder.getKey().longValue();
		question.setQuestionId(generatedId);
		return generatedId;
	}

	public int updateQuestion(QuestionBean question) {
		String sql = """
				UPDATE questions
				SET
				    course_id = ?,
				    question_text = ?,
				    question_type = ?,
				    default_mark = ?,
				    explanation = ?,
				    is_active = ?
				WHERE question_id = ?
				""";

		return jdbcTemplate.update(sql, question.getCourseId(), question.getQuestionText(), question.getQuestionType(),
				question.getDefaultMark(), question.getExplanation(), question.getIsActive(), question.getQuestionId());
	}

	public int deleteQuestion(Long questionId) {
		String sql = """
				DELETE FROM questions
				WHERE question_id = ?
				""";

		return jdbcTemplate.update(sql, questionId);
	}

	public List<QuestionOptionBean> getOptionsByQuestionId(Long questionId) {
		String sql = """
				SELECT
				    option_id,
				    question_id,
				    option_label,
				    option_text,
				    is_correct
				FROM question_options
				WHERE question_id = ?
				ORDER BY option_label
				""";

		return jdbcTemplate.query(sql, questionOptionRowMapper, questionId);
	}

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

		return jdbcTemplate.update(sql, option.getQuestionId(), option.getOptionLabel(), option.getOptionText(),
				option.getIsCorrect());
	}

	public int deleteOptionsByQuestionId(Long questionId) {
		String sql = """
				DELETE FROM question_options
				WHERE question_id = ?
				""";

		return jdbcTemplate.update(sql, questionId);
	}

	public List<QuestionBean> findAll() {

		String sql = """
				SELECT
				    question_id,
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
				WHERE is_active = 1
				ORDER BY question_id DESC
				""";

		return jdbcTemplate.query(sql, new QuestionRowMapper());
	}
}
