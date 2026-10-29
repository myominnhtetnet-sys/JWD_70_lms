package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class Admin_Respority {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AdminBean_category> getAllCategories() {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                     "FROM categories ORDER BY category_id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdminBean_category.class));
    }

    public AdminBean_category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                     "FROM categories WHERE category_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AdminBean_category.class), categoryId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int saveCategory(AdminBean_category category) {
        String sql = "INSERT INTO categories (parent_id, name, slug, description, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (category.getParent_id() != null && category.getParent_id() > 0) {
                ps.setInt(1, category.getParent_id());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, category.getName());
            ps.setString(3, category.getSlug());
            ps.setString(4, category.getDescription());
            ps.setString(5, category.getIs_active() != null ? category.getIs_active() : "1");
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public int updateCategory(AdminBean_category category) {
        String sql = "UPDATE categories SET parent_id = ?, name = ?, slug = ?, description = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE category_id = ?";

        return jdbcTemplate.update(sql,
                (category.getParent_id() != null && category.getParent_id() > 0) ? category.getParent_id() : null,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIs_active(),
                category.getCategory_id()
        );
    }

    public int deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        return jdbcTemplate.update(sql, categoryId);
    }
}