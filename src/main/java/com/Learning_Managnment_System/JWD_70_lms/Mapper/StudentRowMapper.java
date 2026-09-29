package com.Learning_Managnment_System.JWD_70_lms.Mapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;



public class StudentRowMapper implements RowMapper<StudentBean> {

    @Override
    public StudentBean mapRow(ResultSet rs, int rowNum) throws SQLException {

        StudentBean student = new StudentBean();

        student.setId(rs.getInt("id"));
        student.setRole_id(rs.getInt("role_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setAge(rs.getInt("age"));
        student.setAddress(rs.getString("address"));

        Date dob = rs.getDate("dob");

        if (dob != null) {
            student.setDob(dob.toLocalDate());
        }

        student.setGender(rs.getString("gender"));

        return student;
    }
}
