package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;

public class Admin_Mapper implements RowMapper<AdminBean_category> {

    @Override
    public AdminBean_category mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminBean_category category = new AdminBean_category();

        category.setCategory_id(rs.getInt("category_id"));

        // Handle possible NULL for parent_id in database
        int parentId = rs.getInt("parent_id");
        if (rs.wasNull()) {
            category.setParent_id(null);
        } else {
            category.setParent_id(parentId);
        }

        category.setName(rs.getString("name"));
        category.setSlug(rs.getString("slug"));
        category.setDescription(rs.getString("description"));
        category.setIs_active(rs.getInt("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        category.setCreated_at(createdAt);
        category.setUpdated_at(updatedAt);

        // Word Bubble logic using standard Lombok setters
        Instant now = Instant.now();

        if (createdAt != null) {
            long hoursOld = ChronoUnit.HOURS.between(createdAt.toInstant(), now);

            if (updatedAt != null) {
                long diffInSeconds = ChronoUnit.SECONDS.between(createdAt.toInstant(), updatedAt.toInstant());

                // If updated at least 60 seconds after creation, mark EDITED
                if (diffInSeconds > 60) {
                    category.set_edited(false);
                    category.set_new(false);
                } else {
                    category.set_new(false);
                    category.set_edited(false);
                }
            } else {
                category.set_new(false);
                category.set_edited(false);
            }
        }

        return category;
    }
}