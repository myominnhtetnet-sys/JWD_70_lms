
package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;

public class ExamQuestionRowMapper implements RowMapper<ExamQuestionBean> {

    @Override
    public ExamQuestionBean mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        ExamQuestionBean question = new ExamQuestionBean();

        // Question ID
        question.setQuestionId(rs.getLong("question_id"));

        // Exam ID ရှိတဲ့ query တွေအတွက်သာ map လုပ်မယ်
        try {
            question.setExamId(rs.getLong("exam_id"));
        } catch (SQLException e) {
            // Student question query မှာ exam_id မပါရင် ချန်ထားမယ်
        }

        // Question text
        question.setQuestionText(rs.getString("question_text"));

        // Question type
        String rawType = rs.getString("question_type");

        if (rawType != null) {
            question.setQuestionType(rawType.trim().toUpperCase());
        } else {
            question.setQuestionType("MCQ");
        }

        // Mark
        question.setMark(rs.getBigDecimal("mark"));

        // Sort order - query မှာ column ပါမှ map လုပ်မယ်
        try {
            question.setSortOrder(rs.getInt("sort_order"));
        } catch (SQLException e) {
            // Student question query မှာ sort_order မပါရင် ချန်ထားမယ်
        }

        return question;
    }
}
