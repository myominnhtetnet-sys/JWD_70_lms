package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;

public class ExamRowMapper implements RowMapper<ExamBean> {

    @Override
    public ExamBean mapRow(ResultSet rs, int rowNum) throws SQLException {

        ExamBean exam = new ExamBean();

        exam.setExamId(rs.getLong("exam_id"));
        exam.setCourseId(rs.getLong("course_id"));
        exam.setBatchId(rs.getLong("batch_id"));
        exam.setTitle(rs.getString("title"));
        exam.setExamType(rs.getString("exam_type"));
        exam.setDurationMin(rs.getInt("duration_min"));
        exam.setTotalMark(rs.getBigDecimal("total_mark"));
        exam.setPassMark(rs.getBigDecimal("pass_mark"));

        if (rs.getTimestamp("start_at") != null) {
            exam.setStartAt(
                rs.getTimestamp("start_at").toLocalDateTime());
        }

        if (rs.getTimestamp("end_at") != null) {
            exam.setEndAt(
                rs.getTimestamp("end_at").toLocalDateTime());
        }

        exam.setMaxAttempts(rs.getInt("max_attempts"));
        exam.setShuffleQuestions(rs.getBoolean("shuffle_questions"));

        exam.setStatus(rs.getString("status"));
        exam.setCreatedBy(rs.getLong("created_by"));

        if (rs.getTimestamp("created_at") != null) {
            exam.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            exam.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return exam;
    }
}
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
