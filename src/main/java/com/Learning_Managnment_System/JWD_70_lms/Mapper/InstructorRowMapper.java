package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.InstructorBean;

public class InstructorRowMapper implements RowMapper<InstructorBean>{

	@Override
	public InstructorBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		InstructorBean instructor = new InstructorBean();
        instructor.setUserId(rs.getLong("user_id"));
        instructor.setFullName(rs.getString("full_name"));
        return instructor;	}

}
