package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;


@Repository
public class Admin_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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