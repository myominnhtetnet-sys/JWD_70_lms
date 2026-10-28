package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminCourseBean;

public class AdminCourseRowMapper implements RowMapper<AdminCourseBean> {
	
	@Override
    public AdminCourseBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminCourseBean course = new AdminCourseBean();
        course.setCourseId(rs.getLong("course_id"));
        course.setCategoryId(rs.getLong("category_id"));
        course.setCategoryName(rs.getString("category_name"));
        course.setTitle(rs.getString("title"));
        course.setSlug(rs.getString("slug"));
        course.setShortDescription(rs.getString("short_description"));
        course.setDescription(rs.getString("description"));
        course.setThumbnail(rs.getString("thumbnail"));
        course.setLevel(rs.getString("level"));
        course.setLanguage(rs.getString("language"));
        course.setPrice(rs.getBigDecimal("price"));
        course.setDurationWeeks(rs.getInt("duration_weeks"));
        course.setStatus(rs.getString("status"));
        course.setAllowDiscount(rs.getBoolean("allow_discount"));
        course.setAllowInstallment(rs.getBoolean("allow_installment"));
        course.setAllowScholarship(rs.getBoolean("allow_scholarship"));
        course.setCreatedBy(rs.getLong("created_by"));
        
        if (rs.getTimestamp("created_at") != null) {
            course.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            course.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return course;
    }

}
