package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public LoginBean login(String email, String password) {

        String sql = """
              SELECT  u.role_id,
                    u.full_name,
                    u.email,
                    u.password_hash
                FROM users u
                JOIN roles r
                    ON u.role_id = r.role_id
                WHERE u.email =?
                AND u.password_hash =?;
                """;

        try {

            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {

                        LoginBean user = new LoginBean();

                       

                        user.setRole_id(
                                rs.getInt("role_id")
                        );

                        user.setFull_name(
                                rs.getString("full_name")
                        );

                        user.setEmail(
                                rs.getString("email")
                        );

                        user.setPassword_hash(
                                rs.getString("password_hash")
                        );

                       
                        return user;
                    },
                    email,
                    password
            );

        } catch (Exception e) { 
        	System.out.println( "LOGIN ERROR: " + e.getMessage() ); 
        	return null; }
    }
}