package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

public class StudentMapper implements RowMapper<StudentBean> {

    @Override
    public StudentBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        StudentBean student = new StudentBean();

        student.setId(rs.getObject("id") != null ? rs.getLong("id") : null);
        student.setFullName(rs.getString("full_name"));
        student.setUserId(rs.getObject("user_id") != null ? rs.getInt("user_id") : null);
        student.setRoleId(rs.getObject("role_id") != null ? rs.getInt("role_id") : 3);
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setPasswordHash(rs.getString("password_hash"));
        
        // Date & Timestamp mappings safely
        student.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setProfileImage(rs.getString("profile_image"));
        student.setStatus(rs.getString("status"));

        student.setEmailVerifiedAt(rs.getTimestamp("email_verified_at") != null ? rs.getTimestamp("email_verified_at").toLocalDateTime() : null);
        student.setLastLoginAt(rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null);
        student.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        student.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        student.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);

        return student;
    }
}