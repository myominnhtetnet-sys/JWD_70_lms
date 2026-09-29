package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Repository
public class StudentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int saveStudent(StudentBean student) {
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

}
