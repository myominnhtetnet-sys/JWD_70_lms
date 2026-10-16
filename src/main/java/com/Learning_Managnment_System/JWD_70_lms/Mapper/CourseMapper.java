package com.Learning_Managnment_System.JWD_70_lms.Mapper; // Adjust package matching your structure

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

public class CourseMapper implements RowMapper<CourseBean> {

    @Override
    public CourseBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        CourseBean course = new CourseBean();
        
        // Map required structural database table fields to your layout target bean properties
        course.setCourse_id(rs.getLong("course_id"));
        course.setCategory_id(rs.getLong("category_id"));
        course.setTitle(rs.getString("title"));
        course.setSlug(rs.getString("slug"));
        course.setShort_description(rs.getString("short_description"));
        course.setLevel(rs.getString("level"));
        course.setLanguage(rs.getString("language"));
        course.setDuration_weeks(rs.getInt("duration_weeks"));
        course.setPrice(rs.getDouble("price"));
        
        // Map functional bit tracking conditional filter flags
        course.setAllow_discount(rs.getInt("allow_discount"));
        course.setAllow_installment(rs.getInt("allow_installment"));
        course.setAllow_scholarship(rs.getInt("allow_scholarship"));
        
        return course;
    }
}
