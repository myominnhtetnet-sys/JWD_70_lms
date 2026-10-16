package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamQuestionRowMapper implements RowMapper<ExamQuestionBean> {

    @Override
    public ExamQuestionBean mapRow(ResultSet rs, int rowNum) throws SQLException {

        ExamQuestionBean examQuestion = new ExamQuestionBean();

        examQuestion.setExamId(rs.getLong("exam_id"));
        examQuestion.setQuestionId(rs.getLong("question_id"));
        examQuestion.setQuestionText(rs.getString("question_text"));
        examQuestion.setQuestionType(rs.getString("question_type"));
        examQuestion.setMark(rs.getBigDecimal("mark"));
        examQuestion.setSortOrder(rs.getInt("sort_order"));

        return examQuestion;
    }
}
        ExamQuestionBean questionNode = new ExamQuestionBean();
        
        // 🟢 FIXED: Synchronized precisely with your exact camelCase model setters
        questionNode.setQuestionId(rs.getInt("question_id"));
        questionNode.setQuestionText(rs.getString("question_text"));
        
        // Trims white spaces and forces uppercase formatting to match Thymeleaf checks perfectly
        String rawType = rs.getString("question_type");
        if (rawType != null) {
            questionNode.setQuestionType(rawType.trim().toUpperCase());
        } else {
            questionNode.setQuestionType("MCQ");
        }
        
        questionNode.setQuestionMark(((Number) rs.getObject("mark")).doubleValue());
        
        return questionNode;
    }
}
