
package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamQuestionRowMapper implements RowMapper<ExamQuestionBean> {

    @Override
    public ExamQuestionBean mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        ExamQuestionBean question = new ExamQuestionBean();

        question.setExamId(rs.getLong("exam_id"));
        question.setQuestionId(rs.getLong("question_id"));
        question.setQuestionText(rs.getString("question_text"));

        String questionType = rs.getString("question_type");

        if (questionType != null) {
            question.setQuestionType(
                questionType.trim().toUpperCase()
            );
        } else {
            question.setQuestionType("MCQ");
        }

        question.setQuestionMark(
            rs.getDouble("mark")
        );

        question.setSortOrder(
            rs.getInt("sort_order")
        );

        return question;
    }
}
