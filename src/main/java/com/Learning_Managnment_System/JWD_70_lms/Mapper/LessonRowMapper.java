package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.Learning_Managnment_System.JWD_70_lms.model.LessonBean;

@Component
public class LessonRowMapper implements RowMapper<LessonBean> {

    @Override
    public LessonBean mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        LessonBean lesson = new LessonBean();
        lesson.setLessonId(rs.getInt("lesson_id"));
        lesson.setTitle(rs.getString("title"));

        return lesson;
    }
}
