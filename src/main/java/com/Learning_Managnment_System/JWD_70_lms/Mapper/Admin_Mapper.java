package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin_Mapper implements RowMapper<AdminBean_category> {

    @Override
    public AdminBean_category mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminBean_category category = new AdminBean_category();

        category.setId(rs.getInt("id"));
        category.setCategory_id(rs.getInt("category_id"));
        category.setParent_id(rs.getInt("parent_id"));
        category.setName(rs.getString("name"));
        category.setSlug(rs.getString("slug"));
        category.setDescription(rs.getString("description"));
        category.setIs_active(rs.getString("is_active"));
        category.setCreated_at(rs.getString("created_at"));
        category.setUpdated_at(rs.getString("updated_at"));

        return category;
    }
}