package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

public class AdminUserMapper implements RowMapper<AdminUserBean> {

    @Override
    public AdminUserBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminUserBean user = new AdminUserBean();
        
        user.setUser_id(rs.getInt("user_id"));
        user.setRole_id(rs.getInt("role_id"));
        
        try {
            user.setRole_name(rs.getString("role_name"));
        } catch (SQLException e) {
            // Column မပါလာရင် ကျော်ရန်
        }
        
        user.setFull_name(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword_hash(rs.getString("password_hash"));
        
        if (rs.getDate("dob") != null) {
            user.setDob(rs.getDate("dob").toLocalDate());
        }
        
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setStatus(rs.getString("status"));
        
        return user;
    }
}