package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;

public class AdminCategoryMapper implements RowMapper<CategoryBean> {
	
	@Override
    public CategoryBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryBean category = new CategoryBean();
        
        // Database က category_id နဲ့ name ကို ယူပြီး Bean ထဲသို့ ထည့်ခြင်း
        category.setCategory_id(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        return category;
    }

}
