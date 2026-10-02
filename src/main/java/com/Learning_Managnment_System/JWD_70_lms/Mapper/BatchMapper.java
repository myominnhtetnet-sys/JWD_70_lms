package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchMapper implements RowMapper<BatchBean> {

    @Override
    public BatchBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        BatchBean batch = new BatchBean();
        
        batch.setBatchId(rs.getInt("batch_id"));
        batch.setBatchCode(rs.getString("batch_code"));
        batch.setTitle(rs.getString("title"));
        batch.setStartDate(rs.getDate("start_date").toLocalDate());
        batch.setEndDate(rs.getDate("end_date").toLocalDate());
        batch.setScheduleNote(rs.getString("schedule_note"));
        batch.setMaxSeats(rs.getInt("max_seats"));
        batch.setStatus(rs.getString("status"));
        batch.setCourseId(rs.getLong("course_id"));
        
        return batch;
    }
}
