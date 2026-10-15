package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;
import java.util.List;

@Repository
public class Admin_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AdminUserBean> getUsers() {
        String sql = "SELECT u.user_id, u.full_name, u.email, u.status, r.role_name " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdminUserBean.class));
    }

    public AdminUserBean getUserById(int id) {
        String sql = "SELECT user_id, role_id, full_name, email, status FROM users WHERE user_id = ?";
        List<AdminUserBean> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdminUserBean.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void createUser(AdminUserBean user) {
        String sql = "INSERT INTO users (role_id, full_name, email, password_hash, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, 'ACTIVE', NOW(), NOW())";
        jdbcTemplate.update(sql, user.getRole_id(), user.getFull_name(), user.getEmail(), user.getPassword_hash());
    }

    public void updateUser(AdminUserBean user) {
        String sql = "UPDATE users SET role_id = ?, full_name = ?, email = ?, updated_at = NOW() WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getRole_id(), user.getFull_name(), user.getEmail(), user.getUser_id());
    }

    public void updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";
        jdbcTemplate.update(sql, status, userId);
    }
}