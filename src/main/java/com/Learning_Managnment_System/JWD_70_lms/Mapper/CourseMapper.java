package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

public class CourseMapper implements RowMapper<CourseBean>{

	public CourseBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		CourseBean obj = new CourseBean();
		obj.setCourse_id(rs.getInt("course_id"));
		obj.setCategory_id(rs.getInt("course_id"));
		obj.setTitle(rs.getString("title"));
		obj.setSlug(rs.getString("slug"));
		obj.setShort_description(rs.getString("short_description"));
		obj.setDescription(rs.getString("description"));
		obj.setThumbnail(rs.getString("thumbnail"));
		obj.setLevel(rs.getString("level"));
		obj.setLanguage(rs.getString("language"));
		obj.setPrice(rs.getBigDecimal("price"));
		obj.setDuration_weeks(rs.getInt("duration_weeks"));
		obj.setAllow_discount(rs.getInt("allow_discount"));
		obj.setAllow_installment(rs.getInt("allow_installment"));
		obj.setAllow_scholarship(rs.getInt("allow_scholarship"));
		return obj;
	}
	
	
}