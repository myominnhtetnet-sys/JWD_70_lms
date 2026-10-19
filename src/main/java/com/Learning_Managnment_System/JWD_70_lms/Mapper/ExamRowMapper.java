package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamRowMapper implements RowMapper<ExamBean> {
    @Override
    public ExamBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExamBean exam = new ExamBean();
        
        // 🟢 SYNCED: Maps columns directly to your clean snake_case fields
        exam.setExam_id(rs.getInt("exam_id"));
        exam.setCourse_id(rs.getInt("course_id"));
        
        int batchId = rs.getInt("batch_id");
        exam.setBatch_id(rs.wasNull() ? null : batchId);
        
        exam.setTitle(rs.getString("title"));
        exam.setExam_type(rs.getString("exam_type"));
        exam.setDuration_min(rs.getShort("duration_min"));
        exam.setTotal_mark(rs.getBigDecimal("total_mark"));
        exam.setPass_mark(rs.getBigDecimal("pass_mark"));
        exam.setMax_attempts(rs.getByte("max_attempts"));
        
        return exam;
    }
}
