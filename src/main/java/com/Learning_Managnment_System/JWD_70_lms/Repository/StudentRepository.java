package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Repository
public class StudentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createStudent(StudentBean student) {
        String sql = "INSERT INTO users (role_id, full_name, email, phone, password_hash, dob, gender, address, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        return jdbcTemplate.update(
            sql,
            student.getRoleId() != null ? student.getRoleId() : 3,
            student.getFullName(),
            student.getEmail(),
            student.getPhone(),
            student.getPasswordHash(),
            student.getDob(),
            student.getGender() != null ? student.getGender().toUpperCase() : null,
            student.getAddress(),
            student.getStatus() != null ? student.getStatus() : "ACTIVE",
            now,
            now
        );
    }
    public Map<String, Object> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForMap(sql, email);
        } catch (Exception e) {
            return null;
        }
    }

    
    public int updateProfile(String email, String fullName, String phone, String address) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, address = ?, updated_at = ? WHERE email = ?";
        return jdbcTemplate.update(sql, fullName, phone, address, LocalDateTime.now(), email);
    }

   
    public int updatePassword(String email, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, updated_at = ? WHERE email = ?";
        return jdbcTemplate.update(sql, newPasswordHash, LocalDateTime.now(), email);
    }
}
