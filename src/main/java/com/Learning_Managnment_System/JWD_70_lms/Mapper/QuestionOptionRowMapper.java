package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

public class QuestionOptionRowMapper implements RowMapper<QuestionOptionBean> {

    @Override
    public QuestionOptionBean mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        QuestionOptionBean option = new QuestionOptionBean();

        option.setOptionId(rs.getLong("option_id"));
        option.setQuestionId(rs.getLong("question_id"));
        option.setOptionLabel(rs.getString("option_label"));
        option.setOptionText(rs.getString("option_text"));
        option.setIsCorrect(rs.getBoolean("is_correct"));

        return option;
    }
}

