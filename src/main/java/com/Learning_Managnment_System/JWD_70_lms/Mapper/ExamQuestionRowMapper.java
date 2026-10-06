package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;

public class ExamQuestionRowMapper implements RowMapper<ExamQuestionBean> {

    @Override
    public ExamQuestionBean mapRow(ResultSet rs, int rowNum) throws SQLException {

        ExamQuestionBean examQuestion = new ExamQuestionBean();

        examQuestion.setExamId(rs.getLong("exam_id"));
        examQuestion.setQuestionId(rs.getLong("question_id"));
        examQuestion.setMark(rs.getBigDecimal("mark"));
        examQuestion.setSortOrder(rs.getInt("sort_order"));

        return examQuestion;
    }
}