package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;

public class QuestionRowMapper implements RowMapper<QuestionBean> {

    @Override
    public QuestionBean mapRow(ResultSet rs, int rowNum) throws SQLException {

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
            question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            question.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return question;
    }
}

