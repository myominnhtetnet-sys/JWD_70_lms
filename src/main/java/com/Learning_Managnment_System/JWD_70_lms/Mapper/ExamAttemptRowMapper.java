package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ExamAttemptRowMapper implements RowMapper<ExamAttemptBean> {
    @Override
    public ExamAttemptBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExamAttemptBean attempt = new ExamAttemptBean();
        attempt.setAttempt_id(rs.getInt("attempt_id"));
        attempt.setExam_id(rs.getInt("exam_id"));
        attempt.setEnrollment_id(rs.getInt("enrollment_id"));
        attempt.setAttempt_no(rs.getByte("attempt_no"));
        attempt.setScore(rs.getBigDecimal("score"));
        attempt.setResult(rs.getString("result"));
        attempt.setStatus(rs.getString("status"));
        attempt.setRemark(rs.getString("remark"));
        attempt.setSubmitted_at(rs.getTimestamp("submitted_at"));
        
        // 🟢 SYNCED: Maps metadata keys straight into your clean snake_case Lombok properties
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String colName = meta.getColumnName(i).toLowerCase();
            if ("exam_title".equalsIgnoreCase(colName) || "title".equalsIgnoreCase(colName)) {
                attempt.setTitle(rs.getString(i));
            } else if ("total_mark".equalsIgnoreCase(colName)) {
                attempt.setTotal_mark(rs.getBigDecimal(i));
            } else if ("pass_mark".equalsIgnoreCase(colName)) {
                attempt.setPass_mark(rs.getBigDecimal(i));
            }
        }
        return attempt;
    }
}
