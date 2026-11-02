package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class AdminProfileRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Fetch user profile details by ID
    public Map<String, Object> getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (Exception e) {
            return null;
        }
    }

    // 2. Update user's personal information
    public void updateProfile(int userId, String fullName, String phone, String address) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, address = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, fullName, phone, address, userId);
    }

    // 3. Update user's password (matches your database password column name, e.g., password_hash)
    public void updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, newPasswordHash, userId);
    }
}