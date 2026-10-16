package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.Admin_Mapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

@Repository
public class Admin_Repository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Admin_Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==========================================
    // CATEGORY MANAGEMENT METHODS
    // ==========================================

    public List<AdminBean_category> getAllCategories() {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                     "FROM categories ORDER BY category_id DESC";
        return jdbcTemplate.query(sql, new Admin_Mapper());
    }

    public List<AdminBean_category> searchCategories(String name, String slug) {
        StringBuilder sql = new StringBuilder(
                "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                "FROM categories WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND LOWER(name) LIKE LOWER(?) ");
            params.add("%" + name.trim() + "%");
        }

        if (slug != null && !slug.trim().isEmpty()) {
            sql.append("AND LOWER(slug) LIKE LOWER(?) ");
            params.add("%" + slug.trim() + "%");
        }

        sql.append("ORDER BY category_id DESC");

        return jdbcTemplate.query(
                sql.toString(),
                new Admin_Mapper(),
                params.toArray()
        );
    }

    public AdminBean_category getCategoryById(int id) {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                     "FROM categories WHERE category_id = ?";

        List<AdminBean_category> result = jdbcTemplate.query(sql, new Admin_Mapper(), id);

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public List<AdminBean_category> findByParentId(int parentId) {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at " +
                     "FROM categories WHERE parent_id = ? ORDER BY category_id DESC";

        return jdbcTemplate.query(sql, new Admin_Mapper(), parentId);
    }

    public int saveCategory(AdminBean_category category) {
        Integer parentId = category.getParent_id();
        if (parentId == null || parentId <= 0) {
            parentId = null;
        }

        String isActive = category.getIs_active();
        if (isActive == null || isActive.trim().isEmpty()) {
            isActive = "1"; 
        }

        String sql = "INSERT INTO categories (parent_id, name, slug, description, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.update(sql,
                parentId,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                isActive
        );

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    public void updateCategory(AdminBean_category category) {
        Integer parentId = category.getParent_id();
        if (parentId == null || parentId <= 0) {
            parentId = null;
        }

        String isActive = category.getIs_active();
        if (isActive == null || isActive.trim().isEmpty()) {
            isActive = "1";
        }

        String sql = "UPDATE categories SET parent_id = ?, name = ?, slug = ?, description = ?, is_active = ?, updated_at = NOW() " +
                     "WHERE category_id = ?";

        jdbcTemplate.update(sql,
                parentId,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                isActive,
                category.getCategory_id()
        );
    }

    public void deleteCategory(Integer id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ==========================================
    // USER MANAGEMENT METHODS
    // ==========================================

    private final RowMapper<AdminUserBean> userMapper = new RowMapper<AdminUserBean>() {
        @Override
        public AdminUserBean mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdminUserBean u = new AdminUserBean();
            u.setUser_id(rs.getInt("user_id"));
            u.setRole_id(rs.getInt("role_id"));
            u.setRole_name(rs.getString("role_name"));
            u.setFull_name(rs.getString("full_name"));
            u.setEmail(rs.getString("email"));
            u.setPhone(rs.getString("phone"));
            u.setPassword_hash(rs.getString("password_hash"));
            if (rs.getDate("dob") != null) {
                u.setDob(rs.getDate("dob").toLocalDate());
            }
            u.setGender(rs.getString("gender"));
            u.setAddress(rs.getString("address"));
            u.setStatus(rs.getString("status"));
            return u;
        }
    };

    public List<AdminUserBean> searchUsers(String keyword) {
        StringBuilder sql = new StringBuilder("""
            SELECT u.*, r.role_name 
            FROM users u
            LEFT JOIN roles r ON r.role_id = u.role_id
            WHERE u.deleted_at IS NULL
            """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(u.full_name) LIKE ? OR LOWER(u.email) LIKE ? OR LOWER(u.phone) LIKE ?) ");
            String search = "%" + keyword.trim().toLowerCase() + "%";
            params.add(search);
            params.add(search);
            params.add(search);
        }

        sql.append(" ORDER BY u.user_id DESC");

        return jdbcTemplate.query(sql.toString(), userMapper, params.toArray());
    }

    public List<AdminUserBean> getUsers() {
        String sql = """
            SELECT u.*, r.role_name 
            FROM users u
            LEFT JOIN roles r ON r.role_id = u.role_id
            WHERE u.deleted_at IS NULL
            ORDER BY u.user_id DESC
            """;
        return jdbcTemplate.query(sql, userMapper);
    }

    public AdminUserBean getUserById(int userId) {
        String sql = """
            SELECT u.*, r.role_name 
            FROM users u
            LEFT JOIN roles r ON r.role_id = u.role_id
            WHERE u.user_id = ? AND u.deleted_at IS NULL
            """;
        List<AdminUserBean> list = jdbcTemplate.query(sql, userMapper, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int createUser(AdminUserBean u) {
        String sql = """
            INSERT INTO users (role_id, full_name, email, phone, password_hash, dob, gender, address, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')
            """;
        return jdbcTemplate.update(
                sql,
                u.getRole_id(),
                u.getFull_name(),
                u.getEmail(),
                u.getPhone(),
                u.getPassword_hash(),
                u.getDob(),
                u.getGender(),
                u.getAddress()
        );
    }

    public int updateUser(AdminUserBean u) {
        String sql = """
            UPDATE users 
            SET role_id = ?, full_name = ?, email = ?, phone = ?, dob = ?, gender = ?, address = ?
            WHERE user_id = ?
            """;
        return jdbcTemplate.update(
                sql,
                u.getRole_id(),
                u.getFull_name(),
                u.getEmail(),
                u.getPhone(),
                u.getDob(),
                u.getGender(),
                u.getAddress(),
                u.getUser_id()
        );
    }

    public int updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, status, userId);
    }
}