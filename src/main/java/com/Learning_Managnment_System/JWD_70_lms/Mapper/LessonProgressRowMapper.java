package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LessonProgressRowMapper implements RowMapper<LessonProgressBean> {
    @Override
    public LessonProgressBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        LessonProgressBean bean = new LessonProgressBean();
        bean.setLessonId(rs.getInt("lessonId"));
        bean.setLessonTitle(rs.getString("lessonTitle"));
        bean.setContentType(rs.getString("contentType"));
        bean.setDurationMin(rs.getInt("durationMin"));
        bean.setDone(rs.getBoolean("done"));
        return bean;
    }
}
