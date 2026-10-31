package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRowMapper implements RowMapper<CourseBean> {

    @Override
    public CourseBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        CourseBean course = new CourseBean();

        course.setCourse_id(toInteger(rs.getObject("course_id")));
        course.setCategory_id(toInteger(rs.getObject("category_id")));
        course.setTitle(rs.getString("title"));
        course.setSlug(rs.getString("slug"));
        course.setShort_description(rs.getString("short_description"));
        course.setDescription(rs.getString("description"));
        course.setThumbnail(rs.getString("thumbnail"));
        course.setLevel(rs.getString("level"));
        course.setLanguage(rs.getString("language"));
        course.setDuration_weeks(toInteger(rs.getObject("duration_weeks")));

        Object priceObj = rs.getObject("price");
        if (priceObj != null) {
            if (priceObj instanceof Number) {
                course.setPrice(((Number) priceObj).doubleValue());
            } else {
                course.setPrice(Double.parseDouble(priceObj.toString()));
            }
        }

        course.setAllow_discount(toInteger(rs.getObject("allow_discount")));
        course.setAllow_installment(toInteger(rs.getObject("allow_installment")));
        course.setAllow_scholarship(toInteger(rs.getObject("allow_scholarship")));
        course.setStatus(rs.getString("status"));

        try {
            course.setCreated_by(toInteger(rs.getObject("created_by")));
        } catch (SQLException ignored) {}

        try {
            course.setCategory_name(rs.getString("category_name"));
        } catch (SQLException ignored) {}

        try {
            course.setCreated_by_name(rs.getString("created_by_name"));
        } catch (SQLException ignored) {}

        return course;
    }

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj ? 1 : 0;
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}