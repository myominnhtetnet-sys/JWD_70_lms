package com.Learning_Managnment_System.JWD_70_lms.Repository;

<<<<<<< HEAD
<<<<<<< Updated upstream
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import org.springframework.beans.factory.annotation.Autowired;
=======
import java.util.ArrayList;
import java.util.List;

>>>>>>> Stashed changes
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.Admin_Mapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
=======
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
>>>>>>> PaingGyii

@Repository
public class Admin_Repository {

<<<<<<< HEAD
    private final JdbcTemplate jdbcTemplate;

    public Admin_Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

<<<<<<< Updated upstream
    // 1. Fetch all categories ordered by newest first
=======
>>>>>>> Stashed changes
    public List<AdminBean_category> getAllCategories() {
        String sql =
                "SELECT category_id, parent_id, name, slug, " +
                "description, is_active, created_at, updated_at " +
                "FROM categories " +
                "ORDER BY category_id DESC";

        return jdbcTemplate.query(sql, new Admin_Mapper());
    }

<<<<<<< Updated upstream
=======
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

>>>>>>> PaingGyii
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
<<<<<<< HEAD
            category.setIs_active(rs.getString("is_active")); // Fixed column mapping
            return category;
        }, id);
    }
=======
    public List<AdminBean_category> searchCategories(String name, String slug) {
        StringBuilder sql = new StringBuilder(
                "SELECT category_id, parent_id, name, slug, " +
                "description, is_active, created_at, updated_at " +
                "FROM categories WHERE 1=1 "
        );
>>>>>>> Stashed changes

        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND LOWER(name) LIKE LOWER(?) ");
            params.add("%" + name.trim() + "%");
        }

<<<<<<< Updated upstream
       
        String isActive = category.getIs_active();
        if (isActive == null || isActive.trim().isEmpty()) {
            isActive = "1"; 
        }

        String sql = "INSERT INTO categories (parent_id, name, slug, description, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
=======
        if (slug != null && !slug.trim().isEmpty()) {
            sql.append("AND LOWER(slug) LIKE LOWER(?) ");
            params.add("%" + slug.trim() + "%");
        }
>>>>>>> Stashed changes

        sql.append("ORDER BY category_id DESC");

        return jdbcTemplate.query(
                sql.toString(),
                new Admin_Mapper(),
                params.toArray()
        );
    }

    public AdminBean_category getCategoryById(int id) {
        String sql =
                "SELECT category_id, parent_id, name, slug, " +
                "description, is_active, created_at, updated_at " +
                "FROM categories " +
                "WHERE category_id = ?";

        List<AdminBean_category> result = jdbcTemplate.query(sql, new Admin_Mapper(), id);

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public List<AdminBean_category> findByParentId(int parentId) {
        String sql =
                "SELECT category_id, parent_id, name, slug, " +
                "description, is_active, created_at, updated_at " +
                "FROM categories " +
                "WHERE parent_id = ? " +
                "ORDER BY category_id DESC";

        return jdbcTemplate.query(sql, new Admin_Mapper(), parentId);
    }

    public int saveCategory(AdminBean_category category) {
        String sql =
                "INSERT INTO categories " +
                "(parent_id, name, slug, description, is_active, " +
                "created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        return jdbcTemplate.update(
                sql,
                category.getParent_id(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                isActive
               
                
        );
    }

<<<<<<< Updated upstream

    public void updateCategory(AdminBean_category category) {
=======
            category.setIs_active(rs.getString("is_active"));
            return category;
        }, id);
    }

   
    public int saveCategory(AdminBean_category category) {
>>>>>>> PaingGyii
        Integer parentId = category.getParent_id();
        if (parentId == null || parentId <= 0) {
            parentId = null;
        }

<<<<<<< HEAD
        String isActive = category.getIs_active();
        if (isActive == null || isActive.trim().isEmpty()) {
            isActive = "1"; 
=======
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
>>>>>>> PaingGyii
        }

        String sql = "UPDATE categories SET parent_id = ?, name = ?, slug = ?, description = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE category_id = ?";

        jdbcTemplate.update(sql,
                parentId,
<<<<<<< HEAD
=======
    public int updateCategory(AdminBean_category category) {
        String sql =
                "UPDATE categories SET " +
                "parent_id = ?, " +
                "name = ?, " +
                "slug = ?, " +
                "description = ?, " +
                "is_active = ?, " +
                "updated_at = NOW() " +
                "WHERE category_id = ?";

        return jdbcTemplate.update(
                sql,
                category.getParent_id(),
>>>>>>> Stashed changes
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                isActive,
            
=======
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIs_active(),
>>>>>>> PaingGyii
                category.getCategory_id()
        );
    }

<<<<<<< HEAD
<<<<<<< Updated upstream
   
    public void deleteCategory(Integer id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, id);
=======
    public int deleteCategory(int id) {
        String sql =
                "DELETE FROM categories " +
                "WHERE category_id = ?";

        return jdbcTemplate.update(sql, id);
>>>>>>> Stashed changes
=======
  
    public void deleteCategory(Integer id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, id);
>>>>>>> PaingGyii
    }
}