package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;


    public LoginBean login(String email, String rawInputPassword) {
        // 🟢 FIXED: Query by email ONLY to retrieve the record safely
        String sql = "SELECT role_id, user_id, full_name, email, password_hash, status FROM users WHERE email = ? LIMIT 1";
        
        try {
            // 🟢 FIXED: Inline Lambda replaces the missing LoginRowMapper
            LoginBean user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                LoginBean bean = new LoginBean();
                bean.setRole_id(rs.getInt("role_id"));
                bean.setUser_id(rs.getInt("user_id"));
                bean.setFull_name(rs.getString("full_name"));
                bean.setEmail(rs.getString("email"));
                bean.setPassword_hash(rs.getString("password_hash"));
                bean.setStatus(rs.getString("status"));
                return bean;
            }, email);
            
            // 2. Cryptographic Evaluation Matrix Check Block
            if (user != null) {
                // Check if account status profile is allowed access
                if (user.getStatus() != null && !user.getStatus().equalsIgnoreCase("ACTIVE")) {
                    System.err.println("🔒 LOGIN BLOCKED -> Account status is: " + user.getStatus());
                    return null;
                }

                // 🟢 FIXED: Compare the raw form input text with the database BCrypt hash cleanly
                boolean isPasswordCorrect = passwordEncoder.matches(rawInputPassword, user.getPassword_hash());
                
                if (isPasswordCorrect) {
                    return user; // Match verified! Return the data object to log in
                }
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            System.err.println("🔍 LOGIN PIPELINE -> No user account registered under email: " + email);
        } catch (Exception e) {
            System.err.println("❌ CRITICAL LOGIN REPO EXCEPTION -> " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Password incorrect, blocked, or user row not found
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public int updatePassword(String email, String secureHashedPassword) {
        String sql = """
                UPDATE users
                SET password_hash = ?,
                    updated_at = NOW()
                WHERE email = ?
                """;
        return jdbcTemplate.update(sql, secureHashedPassword, email);
    }
    
    
 // =====================================================
    public int resetPassword(String email, String secureHashedPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        return jdbcTemplate.update(sql, secureHashedPassword, email);
    }


    public LoginBean findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND deleted_at IS NULL";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(LoginBean.class), email);
        } catch (Exception e) {
            return null;
        }
    }
    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }
}