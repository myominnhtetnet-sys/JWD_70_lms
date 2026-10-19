package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;

@Component
public class AssignmentRowMapper implements RowMapper<AssignmentBean> {

	@Override
	public AssignmentBean mapRow(ResultSet rs, int rowNum) throws SQLException {

		AssignmentBean assignment = new AssignmentBean();

		assignment.setAssignmentId(rs.getInt("assignment_id"));
		assignment.setBatchId(rs.getInt("batch_id"));
		assignment.setLessonId(rs.getInt("lesson_id"));
		assignment.setTitle(rs.getString("title"));
		assignment.setDescription(rs.getString("description"));
		assignment.setAttachment(rs.getString("attachment"));
		Timestamp startAt = rs.getTimestamp("start_at");

		if (startAt != null) {
			assignment.setStartAt(startAt.toLocalDateTime());
		}

		Timestamp dueAt = rs.getTimestamp("due_at");
		if (dueAt != null) {
			assignment.setDueAt(dueAt.toLocalDateTime());
		}

		Object lateSubmit = rs.getObject("allow_late_submit");
		if (lateSubmit != null) {
			assignment.setAllowLateSubmit(rs.getBoolean("allow_late_submit"));
		}

		Timestamp lateDeadline = rs.getTimestamp("late_deadline");
		if (lateDeadline != null) {
			assignment.setLateDeadline(lateDeadline.toLocalDateTime());
		}

		assignment.setTotalMark(rs.getBigDecimal("total_mark"));
		assignment.setPassMark(rs.getBigDecimal("pass_mark"));
		assignment.setStatus(rs.getString("status"));
		assignment.setCreatedBy(rs.getInt("created_by"));
		Timestamp createdAt = rs.getTimestamp("created_at");

		if (createdAt != null) {
			assignment.setCreatedAt(createdAt.toLocalDateTime());
		}

		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null) {
		    assignment.setUpdatedAt(updatedAt.toLocalDateTime());
		}

		// 🟢 FIXED: Add this block right here to capture and assign the table JOIN fields!
		try {
		    assignment.setBatchCode(rs.getString("batch_code"));
		    assignment.setBatchTitle(rs.getString("batch_title"));
		    assignment.setLessonTitle(rs.getString("lesson_title"));
		} catch (SQLException e) {
		    // This quietly catches the exception if this row mapper is reused 
		    // elsewhere for simple "SELECT * FROM assignments" queries where the JOIN columns don't exist.
		}

		return assignment; // This remains your final line
}
	}