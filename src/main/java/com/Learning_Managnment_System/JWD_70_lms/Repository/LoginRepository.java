package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public LoginBean login(String email, String password) {
        // 🟢 status ကိုပါ SELECT လုပ်ပေးရန် နှင့် deleted_at ကို သုံးရန်
        String sql = """
            SELECT  u.user_id,
                    u.role_id,
                    u.full_name,
                    u.email,
                    u.password_hash,
                    u.status
              FROM users u
              JOIN roles r
                ON u.role_id = r.role_id
              WHERE u.email = ?
              AND u.password_hash = ?
              AND u.deleted_at IS NULL;
              """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                LoginBean user = new LoginBean();
                user.setUser_id(rs.getInt("user_id"));
                user.setRole_id(rs.getInt("role_id"));
                user.setFull_name(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword_hash(rs.getString("password_hash"));
                user.setStatus(rs.getString("status")); // 🟢 status mapping

                return user;
            }, email, password);

        } catch (Exception e) {
            System.out.println("LOGIN ERROR: " + e.getMessage());
            return null;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public int updatePassword(String email, String newPassword) {
        String sql = """
                UPDATE users
                SET password_hash = ?,
                    updated_at = NOW()
                WHERE email = ?
                """;
        return jdbcTemplate.update(sql, newPassword, email);
    }
    
    public int resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        return jdbcTemplate.update(sql, newPassword, email);
    }

    public LoginBean findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND deleted_at IS NULL";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(LoginBean.class), email);
        } catch (Exception e) {
            return null;
        }
    }
}