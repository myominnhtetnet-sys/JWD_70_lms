package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.*;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchMapper {

	public static class BatchRowMapper implements RowMapper<BatchInfo> {
        @Override
        public BatchInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            BatchInfo b = new BatchInfo();
            b.setBatchId(rs.getLong("batch_id"));
            b.setCourseId(rs.getLong("course_id"));
            if (hasColumn(rs, "course_title")) {
                b.setCourseTitle(rs.getString("course_title"));
            }
            b.setBatchCode(rs.getString("batch_code"));
            b.setTitle(rs.getString("title"));
            b.setScheduleNote(rs.getString("schedule_note"));
            if (rs.getDate("start_date") != null) {
                b.setStartDate(rs.getDate("start_date").toLocalDate());
            }
            if (rs.getDate("end_date") != null) {
                b.setEndDate(rs.getDate("end_date").toLocalDate());
            }
            b.setMaxSeats(rs.getInt("max_seats"));
            b.setStatus(rs.getString("status"));
            return b;
        }

        private boolean hasColumn(ResultSet rs, String columnName) {
            try {
                rs.findColumn(columnName);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }
}
