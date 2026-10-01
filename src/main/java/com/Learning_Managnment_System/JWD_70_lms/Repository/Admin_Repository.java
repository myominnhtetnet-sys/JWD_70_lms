package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Admin_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    
    public List<AdminBean_category> getAllCategories() {
        String sql = "SELECT * FROM categories ORDER BY category_id DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AdminBean_category category = new AdminBean_category();
            category.setCategory_id(rs.getInt("category_id"));
            category.setParent_id(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
            category.setName(rs.getString("name"));
            category.setSlug(rs.getString("slug"));
            category.setDescription(rs.getString("description"));
            category.setIs_active(rs.getString("is_active")); // Fixed column mapping
            return category;
        });
    }

    // 2. Fetch single category by ID
    public AdminBean_category getCategoryById(Integer id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            AdminBean_category category = new AdminBean_category();
            category.setCategory_id(rs.getInt("category_id"));
            category.setParent_id(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
            category.setName(rs.getString("name"));
            category.setSlug(rs.getString("slug"));
            category.setDescription(rs.getString("description"));
            category.setIs_active(rs.getString("is_active"));
            return category;
        }, id);
    }

   
    public int saveCategory(AdminBean_category category) {
        Integer parentId = category.getParent_id();
        if (parentId == null || parentId <= 0) {
            parentId = null;
        }

        String sql = "INSERT INTO categories (parent_id, name, slug, description, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.update(sql,
                parentId,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIs_active()
        );

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

  
    public void updateCategory(AdminBean_category category) {
        Integer parentId = category.getParent_id();
        if (parentId == null || parentId <= 0) {
            parentId = null;
        }

        String sql = "UPDATE categories SET parent_id = ?, name = ?, slug = ?, description = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE category_id = ?";

        jdbcTemplate.update(sql,
                parentId,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIs_active(),
                category.getCategory_id()
        );
    }

  
    public void deleteCategory(Integer id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, id);
    }
}