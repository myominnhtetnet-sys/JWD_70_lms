package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.MaterialBean;

public class MaterialRowMapper implements RowMapper<MaterialBean>{

	@Override
	public MaterialBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		MaterialBean material = new MaterialBean();
        material.setMaterialId(rs.getInt("material_id"));
        material.setLessonId(rs.getInt("lesson_id"));
        material.setTitle(rs.getString("title"));
        material.setFilePath(rs.getString("file_path"));
        return material;
        }

}
