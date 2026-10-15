package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 🟢 Dedicated RowMapper for Exam Questions
 * Maps raw JDBC result sets directly into type-safe ExamQuestionBean model instances.
 */
public class ExamQuestionRowMapper implements RowMapper<ExamQuestionBean> {

    @Override
    public ExamQuestionBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExamQuestionBean questionNode = new ExamQuestionBean();
        
        // Maps the primary relational table key identifier
        questionNode.setQuestionId(rs.getInt("question_id"));
        
        // Maps the unique question string text pool block
        questionNode.setQuestionText(rs.getString("question_text"));
        
        // Maps the evaluation system discriminator layout ('MCQ' or 'SHORT_ANSWER')
        questionNode.setQuestionType(rs.getString("question_type"));
        
        // Maps the points weight assigned to this individual question row block
        // Handles type conversions safely across different MySQL database driver version matrix profiles
        questionNode.setQuestionMark(((Number) rs.getObject("points")).doubleValue());
        
        // Note: The List<QuestionOptionBean> options array is left as null here.
        // It is dynamically populated by the repository slice using a distinct subquery.
        return questionNode;
    }
}
