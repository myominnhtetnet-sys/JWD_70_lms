package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchRowMapper implements RowMapper<BatchBean> {

    @Override
    public BatchBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        BatchBean batch = new BatchBean();
        
        // 🟢 FIXED: All setters are renamed to snake_case to match BatchBean fields
        batch.setBatch_id(rs.getLong("batch_id"));
        batch.setCourse_id(rs.getLong("course_id"));
        batch.setBatch_code(rs.getString("batch_code"));
        batch.setTitle(rs.getString("title"));
        batch.setStart_date(rs.getDate("start_date").toLocalDate());
        batch.setEnd_date(rs.getDate("end_date").toLocalDate());
        batch.setSchedule_note(rs.getString("schedule_note"));
        batch.setMax_seats(rs.getInt("max_seats"));
        batch.setStatus(rs.getString("status"));
        batch.setCreated_by(rs.getInt("created_by"));
        
        return batch;
    }
}
